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

    private static final CITY_KEY;
    private static final String CITY_API = "https://api.api-ninjas.com/v1/city";

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
        this.body

    } // ApiApp


    /** {@inheritDoc} */
    @Override
    public void init() {
        root.getChildren().addAll(urlBox, texter, body);
        Insets margin = new Insets(4, 4, 4, 4);
        //first row
        VBox.setMargin(urlBox, margin);
        VBox.setMargin(texter, margin);
        urlBox.getChildren().addAll(getForcast, search, url, getCity);
        HBox.setHgrow(url, Priority.ALWAYS);
        //second row
        texter.getChildren().add(text);


        //body

    }
    /** {@inheritDoc} */
    @Override
    public void start(Stage stage) {

        this.stage = stage;

        // setup scene
        scene = new Scene(root);
        // setup stage
        stage.setTitle("City Weather!");
        stage.setScene(scene);
        stage.setOnCloseRequest(event -> Platform.exit());
        stage.sizeToScene();
        stage.show();

    } // start

    private void getCity() {
        String configPath = "resources/config.properties";
        // the following try-statement is called a try-with-resources statement
        // see https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
        try (FileInputStream configFileStream = new FileInputStream(configPath)) {
            Properties config = new Properties();
            config.load(configFileStream);
            config.list(System.out);                                  // list all using standard out
            CITY_KEY = config.getProperty("apininja.apikey");
        } catch (IOException ioe) {
            System.err.println(ioe);
            ioe.printStackTrace();
        } // try

        try {
            this.getForcast.setDisable(true);
            this.getCity.setDisable(true);
            Platform.runLater(() -> text.setText("Getting City..."));
            // form URI
            String name = URLEncoder.encode(this.url.getText(), StandardCharsets.UTF_8);
            String query = String.format("?name=%s", name);
            this.uri = CITY_API + query;
            System.out.println(uri);
            //build request
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .header("X-Our-Header-1", CITY_KEY)
                .build();
            // send request / receive response in the form of a String
            HttpResponse<String> response = HTTP_CLIENT
                .send(request, BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IOException(response.toString());
            } // if
            // get request body (the content we requested)
            String jsonString = response.body();
            // parse the JSON-formatted string using GSON
            ItunesResponse itunesResponse = GSON
                .fromJson(jsonString, cs1302.gallery.ItunesResponse.class);



            printItunesResponse(itunesResponse);
            this.getForcast.setDisable(false);
            this.getCity.setDisable(false);
            Platform.runLater(() -> text.setText(uri));
        } catch (IOException | InterruptedException | IllegalArgumentException e) {
            // either:
            // 1. an I/O error occurred when sending or receiving;
            // 2. the operation was interrupted; or
            // 3. the Image class could not load the image.
            if (imageArray[0].getImage().getUrl().equals("file:" + DEFAULT_IMG)) {
                this.getImages.setDisable(false);
            } else {
                this.play.setDisable(false);
                this.getImages.setDisable(false);
            }
            Platform.runLater(() -> {
                text.setText("Last attempt to get images failed...");
                this.loadBar.setProgress(1);
                alertError(e, this.uri);
            });
        } // try


    }

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
