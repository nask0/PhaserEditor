
// -- user code here --

/* --- start generated code --- */

// Generated by  1.5.0 (Phaser v2.6.2)


/**
 * EnemyDeath
 * @param {Phaser.Game} aGame A reference to the currently running game.
 * @param {Number} aX The x coordinate (in world space) to position the Sprite at.
 * @param {Number} aY The y coordinate (in world space) to position the Sprite at.
 * @param {any} aKey This is the image or texture used by the Sprite during rendering. It can be a string which is a reference to the Cache entry, or an instance of a RenderTexture or PIXI.Texture.
 * @param {any} aFrame If this Sprite is using part of a sprite sheet or texture atlas you can specify the exact frame to use by giving a string or numeric index.
 */
function EnemyDeath(aGame, aX, aY, aKey, aFrame) {
	Phaser.Sprite.call(this, aGame, aX, aY, aKey || 'atlas', aFrame == undefined || aFrame == null? 'enemy-death/enemy-death-5' : aFrame);
	this.anchor.setTo(0.5, 0.5);
	var _anim_death = this.animations.add('death', ['enemy-death/enemy-death-1', 'enemy-death/enemy-death-2', 'enemy-death/enemy-death-3', 'enemy-death/enemy-death-4', 'enemy-death/enemy-death-5', 'enemy-death/enemy-death-6'], 18, false);
	_anim_death.killOnComplete = true;
	_anim_death.play();
	
}

/** @type Phaser.Sprite */
var EnemyDeath_proto = Object.create(Phaser.Sprite.prototype);
EnemyDeath.prototype = EnemyDeath_proto;
EnemyDeath.prototype.constructor = EnemyDeath;

/* --- end generated code --- */
// -- user code here --
