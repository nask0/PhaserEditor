// The MIT License (MIT)
//
// Copyright (c) 2015, 2017 Arian Fornaris
//
// Permission is hereby granted, free of charge, to any person obtaining a
// copy of this software and associated documentation files (the
// "Software"), to deal in the Software without restriction, including
// without limitation the rights to use, copy, modify, merge, publish,
// distribute, sublicense, and/or sell copies of the Software, and to permit
// persons to whom the Software is furnished to do so, subject to the
// following conditions: The above copyright notice and this permission
// notice shall be included in all copies or substantial portions of the
// Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
// OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
// NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
// DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
// USE OR OTHER DEALINGS IN THE SOFTWARE.
package phasereditor.animation.ui;

import java.util.List;
import java.util.function.Consumer;

import org.eclipse.core.resources.IFile;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.widgets.Composite;

import javafx.animation.Animation.Status;
import javafx.animation.Interpolator;
import javafx.animation.Transition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swt.FXCanvas;
import javafx.util.Duration;
import phasereditor.assetpack.core.animations.AnimationFrameModel;
import phasereditor.assetpack.core.animations.AnimationModel;
import phasereditor.ui.ImageCanvas;

/**
 * @author arian
 *
 */
public class AnimationCanvas extends ImageCanvas implements ControlListener {

	private AnimationModel _animModel;
	private IndexTransition _transition;
	private boolean _showProgress = true;
	private static boolean _initFX;
	protected Runnable _stepCallback;
	protected Consumer<Status> _playbackCallback;
	private ChangeListener<? super Status> _statusListener;
	private int _currentFrame;

	public AnimationCanvas(Composite parent, int style) {
		super(parent, style);

		addControlListener(this);

		if (!_initFX) {
			_initFX = true;
			var temp = new FXCanvas(parent, SWT.NONE);
			temp.dispose();
		}

		_showProgress = true;
	}

	public void play() {
		if (_transition != null) {
			_transition.stop();
		}
		startNewAnimation();
	}

	public void stop() {
		if (_transition != null) {
			_transition.stop();
		}
	}

	public void pause() {
		if (_transition != null) {
			_transition.pause();
		}
	}

	public Runnable getStepCallback() {
		return _stepCallback;
	}

	public void setStepCallback(Runnable stepCallback) {
		_stepCallback = stepCallback;
	}

	public Consumer<Status> getPlaybackCallback() {
		return _playbackCallback;
	}

	public void setPlaybackCallback(Consumer<Status> playbackCallback) {
		_playbackCallback = playbackCallback;
	}

	public IndexTransition getTransition() {
		return _transition;
	}

	public AnimationModel getModel() {
		return _animModel;
	}

	public void setModel(AnimationModel model) {
		setModel(model, true);
	}

	public void setModel(AnimationModel model, boolean autoPlay) {
		_animModel = model;

		if (_transition != null) {
			_transition.stop();
		}

		if (_animModel == null || _animModel.getFrames().isEmpty()) {
			setImageFile((IFile) null);
			return;
		}

		showFrame(0);

		resetZoom();

		if (autoPlay) {
			startNewAnimation();
		}
	}

	private void startNewAnimation() {
		if (_statusListener == null) {
			_statusListener = new ChangeListener<>() {

				@Override
				public void changed(ObservableValue<? extends Status> observable, Status oldValue, Status newValue) {
					if (_playbackCallback != null) {
						_playbackCallback.accept(newValue);
					}
				}
			};
		}

		if (_transition != null) {
			_transition.statusProperty().removeListener(_statusListener);
		}

		_animModel.buildTimeline();

		_transition = new IndexTransition(Duration.millis(_animModel.getComputedTotalDuration()));

		_transition.setDelay(Duration.millis(_animModel.getDelay()));
		_transition.setAutoReverse(_animModel.isYoyo());
		_transition.setCycleCount(_animModel.getRepeat());
		_transition.statusProperty().addListener(_statusListener);

		_transition.play();
	}

	public void showFrame(int index) {
		var animationFrames = _animModel.getFrames();

		if (index >= animationFrames.size()) {
			return;
		}

		var animationFrame = animationFrames.get(index);
		var textureFrame = animationFrame.getFrameAsset();
		if (textureFrame == null) {
			_image = null;
			_frameData = null;
		} else {
			_image = loadImage(textureFrame.getImageFile());
			_frameData = textureFrame.getFrameData();
		}

		_currentFrame = index;
		
		if (!isDisposed()) {
			redraw();
		}
	}
	
	public int getCurrentFrame() {
		return _currentFrame;
	}

	public class IndexTransition extends Transition {

		private int _currentIndex;
		private double _currentFraction;

		public IndexTransition(Duration duration) {
			super();
			setCycleDuration(duration);
			setInterpolator(Interpolator.LINEAR);
			_currentIndex = -1;
		}

		@Override
		protected void interpolate(double frac) {
			_currentFraction = frac;
			int index = 0;

			AnimationModel animModel = getModel();

			if (animModel == null) {
				return;
			}

			List<AnimationFrameModel> frames = animModel.getFrames();

			for (int i = 0; i < frames.size(); i++) {
				var frame = frames.get(i);
				if (frac > frame.getComputedFraction()) {
					index = i;
				} else {
					break;
				}
			}

			if (index != _currentIndex) {
				showFrame(index);
				_currentIndex = index;
			}

			if (!isDisposed()) {
				redraw();
				if (_stepCallback != null) {
					_stepCallback.run();
				}
			}
		}

		public int getCurrentIndex() {
			return _currentIndex;
		}

		public double getFraction() {
			return _currentFraction;
		}
	}

	@Override
	protected void customPaintControl(PaintEvent e) {
		super.customPaintControl(e);

		if (_showProgress) {

			paintProgressLine(e);
		}

	}

	private void paintProgressLine(PaintEvent e) {
		if (_transition != null) {

			e.gc.setForeground(getDisplay().getSystemColor(SWT.COLOR_GREEN));
			e.gc.setBackground(getDisplay().getSystemColor(SWT.COLOR_RED));

			e.gc.setLineWidth(3);

			if (_animModel != null) {

				if (_transition.getStatus() != Status.STOPPED) {
					double frac = _transition.getFraction();
					int x = (int) (frac * e.width);
					e.gc.drawLine(0, e.height - 5, x, e.height - 5);
				}

				e.gc.setAlpha(110);

				for (var frame : _animModel.getFrames()) {
					var frac = frame.getComputedFraction();
					e.gc.fillOval((int) (frac * e.width) - 3, e.height - 3 - 5, 6, 6);
				}
			}
		}
	}

	public void setShowProgress(boolean showProgress) {
		_showProgress = showProgress;
	}

	public boolean isShowProgress() {
		return _showProgress;
	}

	@Override
	public void controlMoved(ControlEvent e) {
		//
	}

	@Override
	public void controlResized(ControlEvent e) {
		// resetZoom();
	}

	public boolean isStopped() {
		return _transition == null || _transition.getStatus() == Status.STOPPED;
	}

	public void playOrPause() {
		if (_animModel == null) {
			return;
		}

		if (_transition == null) {
			play();
			return;
		}

		switch (_transition.getStatus()) {
		case RUNNING:
			_transition.pause();
			break;
		case PAUSED:
			_transition.play();
			break;
		case STOPPED:
			play();
			break;
		default:
			break;
		}
	}

}
