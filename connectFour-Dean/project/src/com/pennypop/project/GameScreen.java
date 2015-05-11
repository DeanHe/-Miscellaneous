package com.pennypop.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.utils.Array;

/**
 * The Game Screen for connect-four, currently it is a 8 * 8 board
 * 
 * @author Dean He
 */
public class GameScreen implements Screen, InputProcessor {

	private ProjectApplication app;

	private final Stage stage;
	private final SpriteBatch spriteBatch;

	private final AssetManager assetManager = new AssetManager();

	private OrthographicCamera camera;
	//
	private final InputMultiplexer plexer;

	int screenHeight;
	int screenWidth;

	private static final String fontPath = "assets/font.fnt";
	private static final String redPath = "assets/red.png";
	private static final String yellowPath = "assets/yellow.png";
	private static final String whitePath = "assets/white.png";
	private static final String uiskinPath = "assets/uiskin.json";

	// number of squares on edge of board
	public final int circlesOnHeight = 8;
	public final int circlesOnWidth = 8;
	// size of each square
	private int circleHeight;
	private int circleWidth;
	// the status label
	private Label title;
	private BitmapFont font;
	// texture of the piece
	private Texture redCircleTexture;
	private Texture yellowCircleTexture;
	private Texture whiteCircleTexture;

	// skin for exit dialog
	private Skin windowSkin;

	// the touch position of mouse
	private Vector3 touchPosition;
	// a container of all chess pieces
	private Array<Piece> pieceContainer;

	private int currentPlayer;
	// default winner player value
	private int WINNER;
	Board board;

	public GameScreen(ProjectApplication app) {
		this.app = app;

		// set up the chain of input processors
		plexer = new InputMultiplexer();
		plexer.addProcessor(this);

		assetManager.load(fontPath, BitmapFont.class);
		assetManager.load(redPath, Texture.class);
		assetManager.load(yellowPath, Texture.class);
		assetManager.load(whitePath, Texture.class);
		assetManager.load(uiskinPath, Skin.class);
		assetManager.finishLoading();

		screenHeight = Gdx.graphics.getHeight();
		screenWidth = Gdx.graphics.getWidth();

		font = assetManager.get(fontPath, BitmapFont.class);
		LabelStyle labelStyle = new LabelStyle(font, Color.CYAN);
		title = new Label("first player's turn", labelStyle);
		redCircleTexture = assetManager.get(redPath, Texture.class);
		yellowCircleTexture = assetManager.get(yellowPath, Texture.class);
		whiteCircleTexture = assetManager.get(whitePath, Texture.class);
		// ui_greyAtlas = assetManager.get(ui_grayPath, TextureAtlas.class);
		windowSkin = assetManager.get(uiskinPath, Skin.class);

		// add Sprite to container
		createPieceContainer();
		circleHeight = redCircleTexture.getHeight();
		circleWidth = redCircleTexture.getWidth();

		// initial state the first player play
		board = new Board(circlesOnHeight, circlesOnWidth);
		currentPlayer = 1;
		WINNER = 0;

		// create camera and spritebatch
		spriteBatch = new SpriteBatch();
		touchPosition = new Vector3();
		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenWidth, screenHeight);

		// initialize stage
		stage = new Stage(screenWidth, screenHeight, false, spriteBatch);
		stage.setCamera(camera);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		stage.dispose();
		assetManager.dispose();
	}

	@Override
	public void render(float delta) {

		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);

		// draw the circles
		spriteBatch.begin();
		for (int x = 0; x < circlesOnWidth; x++) {
			for (int y = 0; y < circlesOnHeight; y++) {
				Sprite circle = pieceContainer.get(y * circlesOnWidth + x).sprite;
				circle.setX(screenWidth / 4f + x * circleWidth);
				circle.setY(screenHeight / 8f + y * circleHeight);
				circle.draw(spriteBatch);
			}
		}
		spriteBatch.end();

		// draw UI
		stage.act(delta);
		stage.draw();

		// check if someone win the game and exit
		displayWin();
	}

	@Override
	public void resize(int width, int height) {
		stage.setViewport(width, height, false);
	}

	@Override
	public void hide() {
		Gdx.input.setInputProcessor(null);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(plexer);

		Table mainTable = new Table();
		mainTable.align(Align.top);
		mainTable.setFillParent(true);
		mainTable.add(title);
		stage.addActor(mainTable);
	}

	@Override
	public void pause() {
		// Irrelevant on desktop, ignore this
	}

	@Override
	public void resume() {
		// Irrelevant on desktop, ignore this
	}

	private void createPieceContainer() {
		pieceContainer = new Array<Piece>();
		for (int x = 0; x < circlesOnWidth; x++) {
			for (int y = 0; y < circlesOnHeight; y++) {
				Sprite sprite = new Sprite(whiteCircleTexture);
				pieceContainer.add(new Piece(sprite));
			}
		}
	}

	@Override
	public boolean keyDown(int paramInt) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char paramChar) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int paramInt) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int paramInt) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		touchPosition.set(screenX, screenY, 0);
		camera.unproject(touchPosition);
		int size = pieceContainer.size;
		for (int i = 0; i < size; i++) {
			Piece item = pieceContainer.get(i);
			// check on each sprite if being clicked
			Rectangle spriteBounds = item.sprite.getBoundingRectangle();
			if (spriteBounds.contains(touchPosition.x, touchPosition.y)) {
				// check if this slot has been filled before
				if (item.occupied == false) {
					// create a new chess piece to fill the UI
					Piece newPiece = new Piece(fillPiece());
					// update this piece status
					newPiece.occupied = true;
					// add to the container for drawing later
					pieceContainer.set(i, newPiece);

					// add the data to grid to check
					board.setGrid(i, currentPlayer);
					if (board.checkWin()) {
						WINNER = currentPlayer;
					}
					switchPlayer();
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean touchDragged(int paramInt1, int paramInt2, int paramInt3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
		// TODO Auto-generated method stub
		return false;
	}

	private Sprite fillPiece() {
		if (currentPlayer == 1) {
			return new Sprite(yellowCircleTexture);
		} else if (currentPlayer == 2) {
			return new Sprite(redCircleTexture);
		} else {
			System.out.println("player state error");
			return null;
		}
	}

	private void switchPlayer() {
		if (currentPlayer == 1) {
			// switch to player 2
			currentPlayer = 2;
			title.setText("Second Player's turn");
		} else if (currentPlayer == 2) {
			// switch to player 1
			currentPlayer = 1;
			title.setText("First Player's turn");
		} else {
			System.out.println("player state error");
		}
	}

	private void displayWin() {
		if (WINNER == 0) {
			return;
		} else {

			String title;
			if (WINNER == 1) {
				title = "first player win !";
			} else if (WINNER == 2) {
				title = "second player win !";
			} else {
				title = "Error";
			}
			// create an exit dialog with winner display
			Dialog dialog = new Dialog(title, windowSkin) {
				{
					button("Return");

				}

				@Override
				protected void result(Object object) {
					app.setMainScreen();
				}

			};
			dialog.show(stage);
			// set input processor back to stage, in order for dialog to work
			Gdx.input.setInputProcessor(stage);
		}
	}

	// the chess piece class
	private class Piece {
		Sprite sprite;
		// check if the piece position is taken
		boolean occupied;

		public Piece(Sprite sprite) {
			this.sprite = sprite;
			occupied = false;
		}
	}

}