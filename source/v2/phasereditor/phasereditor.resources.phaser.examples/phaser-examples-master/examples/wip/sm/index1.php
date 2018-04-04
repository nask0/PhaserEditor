<!doctype html>
<html>
    <head>
        <meta charset="UTF-8" />
        <title>phaser scaling example 1</title>
        <!-- <meta name="viewport" content="initial-scale=1 maximum-scale=1 user-scalable=0 minimal-ui" /> -->
        <?php
            if (($_SERVER['SERVER_NAME'] === '192.168.0.100' || $_SERVER['SERVER_NAME'] === 'localhost'))
            {
                $path = '../../../../phaser';
                require('../../../../phaser/build/config.php');
            }
            else
            {
                $v = "2.1.0";
                echo "<script src=\"_site/phaser/phaser.{$v}.min.js\" type=\"text/javascript\"></script>";
            }
        ?>
        <style>
            body {
                margin: 0;
                background-color: black;
            }
        </style>
    </head>
    <body>

        <script type="text/javascript">

        var game = new Phaser.Game(800, 600, Phaser.CANVAS, '', { init: init, preload: preload, create: create, update: update });

        function init() {

            //  The NO_SCALE scale mode will do pretty much as it says on the tin: it won't ever scale your game, either
            //  up or down. Should the browser (or parent) become too small your game is simply cropped, or scroll bars appear.
            //  This is the default Phaser scale mode. The alignment values control horizontal and vertical spacing within the parent container.

            game.scale.scaleMode = Phaser.ScaleManager.NO_SCALE;
            game.scale.pageAlignVertically = true;
            game.scale.pageAlignHorizontally = true;

        }

        function preload() {
         
            game.load.image('sao', '../../assets/pics/sword_art_online.jpg');
         
        }

        var info;
        var s;

        function create() {

            var pic = game.add.sprite(game.world.centerX, game.world.centerY, 'sao');
            pic.anchor.set(0.5);

            info = game.add.text(32, 32, ' ');
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

        </script>

    </body>
</html>