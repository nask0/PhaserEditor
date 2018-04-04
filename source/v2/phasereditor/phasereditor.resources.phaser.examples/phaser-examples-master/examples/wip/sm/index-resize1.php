<!doctype html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>phaser scaling example</title>
        <!-- <meta name="viewport" content="initial-scale=1 maximum-scale=1 user-scalable=0 minimal-ui" /> -->
        <?php
            if (($_SERVER['SERVER_NAME'] === '192.168.0.100' || $_SERVER['SERVER_NAME'] === 'localhost'))
            {
                $path = '../../../../phaser';
                require('../../../../phaser/build/config.php');
            }
            else
            {
                $v = "2.2.0";
                echo "<script src=\"_site/phaser/phaser.{$v}.min.js\" type=\"text/javascript\"></script>";
            }
        ?>
        <link rel="stylesheet" href="scales.css" type="text/css" charset="utf-8" />
    </head>
    <body>

        <script type="text/javascript">

        var game = new Phaser.Game(800, 600, Phaser.CANVAS, '', { init: init, preload: preload, create: create, update: update, resize: resize });

        function init() {

            game.scale.scaleMode = Phaser.ScaleManager.RESIZE;
            game.stage.backgroundColor = '#5b0c26';

        }

        function preload() {
         
            game.load.image('sao', '../../assets/pics/sword_art_online.jpg');
         
        }

        var pic;
        var info;
        var s;

        function create() {

            pic = game.add.sprite(game.world.centerX, game.world.centerY, 'sao');
            pic.anchor.set(0.5);

            info = game.add.text(16, 16, ' ');
            info.font = "Courier";
            info.fontSize = 14;
            info.fill = "#ffffff";
            info.lineSpacing = 4;
            info.setShadow(2, 2);

        }

        function update() {

            s = "Game size: " + game.width + " x " + game.height + "\n";
            s = s.concat("Actual size: " + game.scale.width + " x " + game.scale.height + "\n");
            s = s.concat("minWidth: " + game.scale.minWidth + " - minHeight: " + game.scale.minHeight + "\n");
            s = s.concat("maxWidth: " + game.scale.maxWidth + " - maxHeight: " + game.scale.maxHeight + "\n");
            s = s.concat("aspect ratio: " + game.scale.aspectRatio + "\n");
            s = s.concat("parent is window: " + game.scale.parentIsWindow + "\n");
            s = s.concat("bounds x: " + game.scale.bounds.x + " y: " + game.scale.bounds.y + " width: " + game.scale.bounds.width + " height: " + game.scale.bounds.height + "\n");

            info.text = s;

        }

        function resize() {

            pic.x = game.world.centerX;
            pic.y = game.world.centerY;

        }

        </script>

    </body>
</html>