
// -- user code here --

/* --- start generated code --- */

// Generated by Phaser Editor 1.4.3 (Phaser v2.6.2)


/**
 * Boot.
 */
class Boot extends Phaser.State {
	
	constructor() {
		
		super();
		
	}
	
	init() {
		
	}
	
	preload () {
		
		this.load.pack('boot', 'assets/pack.json');
		
	}
	
	create() {
		
		// all the assets used in the splash are loaded
		// so jump to the Preloader state
		this.game.state.start("Preloader");
		
		
		
	}
	
	
	/* state-methods-begin */
	// -- user code here --
	/* state-methods-end */
	
}
/* --- end generated code --- */
// -- user code here --
