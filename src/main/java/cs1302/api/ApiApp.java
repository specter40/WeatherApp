package cs1302.api;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.layout.Priority;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import javafx.stage.Modality;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.util.Duration;
import javafx.stage.Modality;
import java.io.FileInputStream;
import java.util.Properties;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


/**
 * REPLACE WITH NON-SHOUTING DESCRIPTION OF YOUR APP.
 */
public class ApiApp extends Application {
    /** HTTP client. */
    public static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_2)           // uses HTTP protocol version 2 where possible
        .followRedirects(HttpClient.Redirect.NORMAL)  // always redirects, except from HTTPS to HTTP
        .build();                                     // builds and returns a HttpClient object

    /** Google {@code Gson} object for parsing JSON-formatted strings. */
    public static Gson GSON = new GsonBuilder()
        .setPrettyPrinting()                          // enable nice output when printing
        .create();                                    // builds and returns a Gson object

    private static final String CITY_KEY = "gOBBQcaVfcJd6pJLqQJ1VA==vx6Y5tWjkbZ23sVd";
    private static final String CITY_API = "https://api.api-ninjas.com/v1/city";


    private static final String WEATHER_KEY = "0a35b43955554910b9d173026230205";
    private static final String WEATHER_API = "http://api.weatherapi.com/v1/current.json";
    Stage stage;
    Scene scene;
    VBox root;

    //Top row
    private Label search;
    private Button getForcast;
    private HBox urlBox;
    private Button getCity;

    private TextField url;
    private boolean player;
    private String uri;

    //2nd row
    private HBox texter;
    private Label text;

    //Text
    private TextFlow body;

    //Lat and Long
    private double latitude

    //Icon
    private ImageView viewer;

    //Weather info
    Textflow weatherInfo;

    /**
     * Constructs an {@code ApiApp} object. This default (i.e., no argument)
     * constructor is executed in Step 2 of the JavaFX Application Life-Cycle.
     */
    public ApiApp() {
        //first row
        root = new VBox();
        getForcast = new Button("Get Forcast");
        this.search = new Label("Search:");
        Font sizer = new Font(14);
        this.search.setFont(sizer);
        this.uri = "";
        this.urlBox = new HBox(4);
        getCity = new Button("Get City");
        this.url = new TextField();
        //2nd row initialized
        this.text = new Label("Type in a city and click the button.");
        this.texter = new HBox(8);

        //Text
        this.body = new TextFlow();

        //Icon
        this.viewer = new ImageView();

        //Weather info
        this.weatherInfo = new TextFlow()
    } // ApiApp


    /** {@inheritDoc} */
    @Override
    public void init() {
        root.getChildren().addAll(urlBox, texter, body, viewer, weatherInfo);
        Insets margin = new Insets(4, 4, 4, 4);
        //first row
        VBox.setMargin(urlBox, margin);
        VBox.setMargin(texter, margin);
        urlBox.getChildren().addAll(getForcast, search, url, getCity);
        HBox.setHgrow(url, Priority.ALWAYS);
        //second row
        texter.getChildren().add(text);
        //body
        this.body.setMaxWidth(420);
        Text starter = new Text("City Weather!");
        this.body.setContent(starter);
        getForcast.setDisable(true);

        //image

        Runnable cities = () -> loadCity();
        this.getCity.setOnAction(event -> runNow(cities));

        Runnable weathery = () -> loadWeather();
        this.getWeather.setOnAction(event -> runNow(weathery));
    }

    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;

        // setup scene
        scene = new Scene(root, 640, 600);
        // setup stage
        stage.setTitle("City Weather!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start

    private void loadCity() {
        try {
            this.getForcast.setDisable(true);
            this.getCity.setDisable(true);
            Platform.runLater(() -> this.body.getChildren().clear());
            Platform.runLater(() -> text.setText("Getting City..."));
            // form URI
            String name = URLEncoder.encode(this.url.getText(), StandardCharsets.UTF_8);

            String query = String.format("?name=%s", name);
            this.uri = CITY_API + query;
            System.out.println(uri);
            //build request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("X-Api-Key", CITY_KEY)
                .build();
            // send request / receive response in the form of a String
            HttpResponse<String> response = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException(response.toString());
            } // if
            // get request body (the content we requested)
            String json = response.body();
            // parse the JSON-formatted string using GSON
            //CityResponse[] cityResponse = CityResponse.fromJson(json);
            CityResponse[] cityResponse = GSON
              .fromJson(json, CityResponse[].class);

            this.getForcast.setDisable(false);
            this.getCity.setDisable(false);
            this.latitude = cityResponse[0].latitude;
            this.longitude = cityResponse[0].longitude;
            printCity(cityResponse);
            Platform.runLater(() -> text.setText(uri));
        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            // either:
            // 1. an I/O error occurred when sending or receiving;
            // 2. the operation was interrupted; or
            // 3. the Image class could not load the image.
            Platform.runLater(() -> {
                text.setText("Last attempt to get city failed...");
                alertError(e, this.uri);
                this.getForcast.setDisable(false);
                this.getCity.setDisable(false);
            });
        } // try
    }

    private void printCity(CityResponse[] cityResponse) {
        Platform.runLater(() -> body.getChildren().add(new Text("City: " + cityResponse[0].name
        + "\nLatitude: " + cityResponse[0].latitude + "\nLongitude: " + cityResponse[0].longitude
        + "\nPopulation: " + cityResponse[0].population)));

    }


    private void loadWeather() {
        try {
            this.getForcast.setDisable(true);
            this.getCity.setDisable(true);

            Platform.runLater(() -> text.setText("Getting Weather..."));
            // form URI
            String q = URLEncoder.encode(latitude + "," + longitude,
            StandardCharsets.UTF_8);
            String key = URLEncoder.encode(WEATHER_KEY ,StandardCharsets.UTF_8);

            String query = String.format("?key=%s&q=%s", key, q);
            this.uri = WEATHER_API + query;
            System.out.println(uri);
            //build request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();
            // send request / receive response in the form of a String
            HttpResponse<String> response = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException(response.toString());
            } // if
            // get request body (the content we requested)
            String json = response.body();
            // parse the JSON-formatted string using GSON
            //CityResponse[] cityResponse = CityResponse.fromJson(json);
            WeatherResponse weatherResponse = GSON
              .fromJson(json, WeatherResponse.class);

            this.getForcast.setDisable(false);
            this.getCity.setDisable(false);
            printWeather(weatherResponse);
            Platform.runLater(() -> text.setText(uri));
        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            // either:
            // 1. an I/O error occurred when sending or receiving;
            // 2. the operation was interrupted; or
            // 3. the Image class could not load the image.
            Platform.runLater(() -> {
                text.setText("Last attempt to get city failed...");
                alertError(e, this.uri);
                this.getForcast.setDisable(false);
                this.getCity.setDisable(false);
            });
        } // try
    }


    private void printWeather(WeatherResponse weather) {
        Image icon = new Image(weather.current.condition.icon);


    }


    public static void alertError(Throwable cause, String url) {
        TextArea text = new TextArea(url + "\n\n"  + cause.toString());
        text.setEditable(false);
        text.setWrapText(true);
        Alert alert = new Alert(AlertType.ERROR);
        alert.getDialogPane().setContent(text);
        alert.setResizable(true);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    } // alertError


    /**
     * Creates and immediately starts a new daemon thread that executes
     * {@code target.run()}. This method, which may be called from any thread,
     * will return immediately its the caller.
     * @param target the object whose {@code run} method is invoked when this
     *               thread is started
     */
    private void runNow(Runnable target) {
        Thread t = new Thread(target);
        t.setDaemon(true);
        t.start();
    } // runNow


} // ApiApp
