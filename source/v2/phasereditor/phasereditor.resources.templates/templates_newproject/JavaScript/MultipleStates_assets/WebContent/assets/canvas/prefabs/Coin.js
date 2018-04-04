
// -- user code here --

/* --- start generated code --- */

// Generated by Phaser Editor 1.4.3 (Phaser v2.6.2)


/**
 * Coin.
 * @param {Phaser.Game} aGame A reference to the currently running game.
 * @param {Number} aX The x coordinate (in world space) to position the Sprite at.
 * @param {Number} aY The y coordinate (in world space) to position the Sprite at.
 * @param {any} aKey This is the image or texture used by the Sprite during rendering. It can be a string which is a reference to the Cache entry, or an instance of a RenderTexture or PIXI.Texture.
 * @param {any} aFrame If this Sprite is using part of a sprite sheet or texture atlas you can specify the exact frame to use by giving a string or numeric index.
 */
function Coin(aGame, aX, aY, aKey, aFrame) {
	
	var pKey = aKey === undefined? 'coins' : aKey;
	var pFrame = aFrame === undefined? 0 : aFrame;
	
	Phaser.Sprite.call(this, aGame, aX, aY, pKey, pFrame);
	this.scale.setTo(0.5, 0.5);
	var _anim_rotate = this.animations.add('rotate', [0, 1, 2, 3, 4, 5], 5, true);
	
	// public fields
	
	this.fCoins = this;
	this.fAnim_rotate = _anim_rotate;
	
	// my code after objects creation
	this.afterCreate();
	
}

/** @type Phaser.Sprite */
var Coin_proto = Object.create(Phaser.Sprite.prototype);
Coin.prototype = Coin_proto;
Coin.prototype.constructor = Coin;

/* --- end generated code --- */

Coin.prototype.afterCreate = function() {
	this.fAnim_rotate.play();
};
