package src.view.scene.game;

import javafx.geometry.Pos;
import javafx.scene.Camera;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.Material;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.Parent;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Line;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.FileInputStream;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.EnumMap;
import java.util.function.Consumer;

import src.model.board.Case;
import src.model.board.LineWall;
import src.model.ContentMaze;
import src.model.MainMaze;
import src.model.MazeDimension;
import src.model.Player;
import src.view.scene.Pause;
import src.view.scene.ScenePlus;
import src.view.scene.Winner;
import src.view.View;

public class Init
{
	// Une camera avec les bons attributs pour la déplacer, et une lumière associée
	public static Group makeSpecialCases(Scale sc, MainMaze maze)
	{
		Group spc = new Group();

		for (Case ec: maze.getContentMazeCurrentLevel().getSpecialCases())
		{
			final float tailleCase = 1f;
			final Box   res        = new Box(tailleCase, 0.01f, tailleCase);

			res.setTranslateX(ec.getX() + tailleCase / 2.0);
			res.setTranslateZ(ec.getY() + tailleCase / 2.0);
			res.setTranslateY(0.5);
			res.setMaterial(new PhongMaterial(CaseColor.getColor(ec.type)));
			spc.getChildren().add(res);
		}
		spc.getTransforms().add(sc);
		return (spc);
	}

	/**
	 * Dessine le sol
	 * @return Un groupe contenant le sol
	 */
	public static Group makeFloors(Scale sc, MainMaze maze)
	{
		final Group floors = new Group();

		floors.getTransforms().add(sc);
		ContentMaze[] cms = maze.getContentMaze();
		for (int i = 0; i < cms.length; i++)
		{
			for (MazeDimension.RectInMaze md: cms[i].getMazeDimension().list_rectmaze)
			{
				final int w = md.x2 - md.x1;
				final int h = md.y2 - md.y1;
				Box       f = new Box(w, 0.5, h);
				f.setTranslateX(md.x1 + w / 2.0);
				f.setTranslateZ(md.y1 + h / 2.0);
				f.setMaterial(new PhongMaterial(Color.color(0.15, 0.15, 0.15)));
				f.setTranslateY(0.5 - i);
				floors.getChildren().add(f);
			}
		}
		return (floors);
	}

	/**
	 * Dessine les murs du Maze
	 * @return Le groupe contenant les murs
	 */
	public static Group makeWalls(Scale sc, MainMaze maze)
	{
		Material mat;

		try{
			String texturePath = maze.getContentMazeCurrentLevel().getTexturePath();
			if (texturePath == null)
			{
				texturePath = "assets/Brick_wall_002_COLOR.jpg";          //Default
			}
			Image img = new Image(new FileInputStream(texturePath), 400, 400, true, false);
			mat = new PhongMaterial(Color.WHITE, img, null, null, null);
		}
		catch (Exception e) {
			mat = new PhongMaterial(Color.GREEN);
		}
		Group walls = new Group();

		// On scale les murs
		walls.getTransforms().add(sc);
		final float   delta = 0.01f;
		ContentMaze[] cms   = maze.getContentMaze();
		for (int i = 0; i < cms.length; i++)
		{
			final LineWall[] lineWalls = LineWall.breakWallsIntoSimpleOnes(cms[i].getLineWalls());
			for (LineWall l: lineWalls)
			{
				Box w = new Box();

				Consumer <Float> setEpais;
				Consumer <Float> setLarg;

				float  largeur;
				double trX;
				double trZ;
				if (!l.isHorizontal())                                            // Mur "vertical" dans le plan
				{
					largeur  = l.getY2() - l.getY1() + l.getEpaisseur() - 2 * delta;
					setLarg  = w::setDepth;
					setEpais = w::setWidth;
					trX      = l.getX1() + l.getEpaisseur() / 2.0 - delta;
					trZ      = l.getY1() + largeur / 2.0;
				}
				else // Mur horizontal
				{
					largeur  = l.getX2() - l.getX1() + l.getEpaisseur() - 2 * delta;
					setLarg  = w::setWidth;
					setEpais = w::setDepth;
					trX      = l.getX1() + largeur / 2.0;
					trZ      = l.getY1() + l.getEpaisseur() / 2.0 - delta;
				}

				w.setHeight(1);
				setEpais.accept(l.getEpaisseur());
				setLarg.accept(largeur);
				w.setTranslateX(trX);
				w.setTranslateZ(trZ);
				w.setTranslateY(-i);

				w.setMaterial(mat);
				walls.getChildren().add(w);
			}
		}
		return (walls);
	}
}
