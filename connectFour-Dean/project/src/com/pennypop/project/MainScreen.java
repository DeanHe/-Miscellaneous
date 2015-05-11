package com.pennypop.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.OrderedMap;

/**
 * This is where you screen code will go, any UI should be in here
 * 
 * @author Richard Taylor
 */
public class MainScreen implements Screen {

	private ProjectApplication app;

	private final Stage stage;
	private final SpriteBatch spriteBatch;

	private final AssetManager assetManager = new AssetManager();

	// declare assets file path
	private static final String TITLE = "PennyPop";
	private static final String fontPath = "assets/font.fnt";
	private static final String apiButtonPath = "assets/apiButton.png";
	private static final String button_clickPath = "assets/button_click.wav";
	private static final String sfxButtonPath = "assets/sfxButton.png";
	private static final String gameButtonPath = "assets/gameButton.png";
	private static final String URL = "http://api.openweathermap.org/data/2.5/weather?q=San%20Francisco,US";

	// asset materials
	private BitmapFont font;
	private ImageButton sfxButton, apiButton, gameButton;
	// Strings of weather API
	private String[] apiInfoStrings;
	// used for screen layout
	private Table stageTable;

	public MainScreen(ProjectApplication app) {
		this.app = app;

		// load assets assetManager
		assetManager.load(fontPath, BitmapFont.class);
		assetManager.load(apiButtonPath, Texture.class);
		assetManager.load(sfxButtonPath, Texture.class);
		assetManager.load(gameButtonPath, Texture.class);
		assetManager.load(button_clickPath, Sound.class);
		assetManager.finishLoading();

		spriteBatch = new SpriteBatch();
		stage = new Stage(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false, spriteBatch);
	}

	@Override
	public void dispose() {
		spriteBatch.dispose();
		stage.dispose();
		assetManager.dispose();
	}

	@Override
	public void render(float delta) {
		stage.act(delta);
		stage.draw();
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
		Gdx.input.setInputProcessor(stage);

		// create the Title label
		font = assetManager.get(fontPath, BitmapFont.class);
		LabelStyle labelStyle = new LabelStyle(font, Color.RED);
		Label titleLabel = new Label(TITLE, labelStyle);

		// weather information variable : city, weather, wind(degree, speed)
		apiInfoStrings = new String[3];

		// create the sfxButton by texture and drawable
		sfxButton = createButton(assetManager.get(sfxButtonPath, Texture.class));
		// register the listener to button
		sfxButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				// TODO Auto-generated method stub
				super.clicked(event, x, y);
				Sound wavSound = assetManager.get(button_clickPath, Sound.class);
				wavSound.play();
			}

		});

		// create the apiButton
		apiButton = createButton(assetManager.get(apiButtonPath, Texture.class));
		apiButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				apiJSONRequest();

			}

		});

		// create the gameButton
		gameButton = createButton(assetManager.get(gameButtonPath, Texture.class));
		gameButton.addListener(new ClickListener() {

			@Override
			public void clicked(InputEvent event, float x, float y) {
				super.clicked(event, x, y);
				// load the connect-four game
				app.setGameScreen();
			}

		});
		// set up the stageTable as the main screen layout
		stageTable = stageLayout(titleLabel);
		stage.addActor(stageTable);
	}

	@Override
	public void pause() {
		// Irrelevant on desktop, ignore this
	}

	@Override
	public void resume() {
		// Irrelevant on desktop, ignore this
	}

	private ImageButton createButton(Texture textTure) {
		// create button by texture and drawable
		TextureRegion image = new TextureRegion(textTure);
		return new ImageButton(new TextureRegionDrawable(image));
	}

	private void apiJSONRequest() {
		// do the http request
		HttpRequest httpRequest = new HttpRequest(Net.HttpMethods.GET);
		httpRequest.setUrl(URL);
		httpRequest.setHeader("Content-Type", "application/json");
		httpRequest.setHeader("Accept", "application/json");
		Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {

			@Override
			public void handleHttpResponse(HttpResponse httpResponse) {
				int statusCode = httpResponse.getStatus().getStatusCode();
				// HttpStatus.SC_OK == 200
				if (statusCode != 200) {
					System.out.println("Request Failed");
					return;
				}

				String responseJson = httpResponse.getResultAsString();
				JsonReader jsonReader = new JsonReader();
				try {
					// can't use jsonValue, something wrong with library
					OrderedMap map = (OrderedMap) jsonReader.parse(responseJson);
					;
					// city
					apiInfoStrings[0] = (String) map.get("name");
					// weather
					Array<OrderedMap> weatherArray = (Array<OrderedMap>) map.get("weather");
					apiInfoStrings[1] = (String) weatherArray.get(0).get("description");
					// wind
					OrderedMap windMap = (OrderedMap) map.get("wind");
					apiInfoStrings[2] = String.format("%.2f degrees, %.2fmph wind",
							windMap.get("deg"), windMap.get("speed"));

					// add the api information to screen
					addWeatherToScreen();
				} catch (Exception exception) {
					exception.printStackTrace();
				}
			}

			@Override
			public void failed(Throwable throwable) {
				System.out.println("Request Failed Completely");

			}
		});
	}

	private Table stageLayout(Label label) {
		// input label is the company title

		// use stage table for the whole layout
		Table stageTable = new Table();
		stageTable.setFillParent(true);

		// use left table for left part of stage
		Table leftTable = new Table();

		// use right table for right part of stage
		Table rightTable = new Table();

		// use another table for buttons layout
		Table buttonTable = new Table();
		buttonTable.align(Align.center);
		buttonTable.add(sfxButton);
		buttonTable.add(apiButton).padLeft(10);
		buttonTable.add(gameButton).padLeft(10);

		// add buttonTable and label to table
		leftTable.center();
		leftTable.add(label).padBottom(20);
		leftTable.row();
		leftTable.add(buttonTable);

		// evenly separate the stage table
		stageTable.add(leftTable).uniform();
		stageTable.add(rightTable).fill(stageTable.getWidth() / 2, stageTable.getHeight())
				.uniform();

		return stageTable;

	}

	private void addWeatherToScreen() {
		// generate the weather info
		LabelStyle weatherTitleStyle = new LabelStyle(font, Color.MAGENTA);
		Label weatherTitle = new Label("Current Weather", weatherTitleStyle);

		LabelStyle cityLabelStyle = new LabelStyle(font, Color.BLUE);
		Label cityLabel = new Label(apiInfoStrings[0], cityLabelStyle);

		LabelStyle weatherLabelStyle = new LabelStyle(font, Color.ORANGE);
		Label weatherLabel = new Label(apiInfoStrings[1], weatherLabelStyle);

		LabelStyle windLabelStyle = new LabelStyle(font, Color.ORANGE);
		Label windLabel = new Label(apiInfoStrings[2], windLabelStyle);
		windLabel.setFontScale(0.8f);

		// the table contains weather api information
		Table rightTable = (Table) stageTable.getChildren().get(1);
		rightTable.reset();
		rightTable.center();

		// add labels to right table
		rightTable.add(weatherTitle);
		rightTable.row();
		rightTable.add(cityLabel).padBottom(20);
		rightTable.row();
		rightTable.add(weatherLabel);
		rightTable.row();
		rightTable.add(windLabel);

	}
}
