package lk.ijse.dep11;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.naming.Binding;
import java.io.File;

public class MainViewController {
    public Button btnStop;
    public Button btnPlay;
    public Button btnBackword;
    public Button btnForward;
    public Button btnMinus;
    public Button btnPlus;
    public MediaView mediaView;
    public Button btnBrowse;
    public BorderPane root;
    public Slider sldVolume;
    public Slider sldProgress;
    public Label lblTime;
    private MediaPlayer mediaPlayer;
    private double rate=1;
    public void initialize(){
        for(Button button : new Button[]{
                btnPlay,btnStop,btnBackword,btnForward,btnMinus,btnPlus})button.setDisable(true);
        sldProgress.setDisable(true);
        sldVolume.setDisable(true);

    }
    public void btnBrowseOnAction(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Media File (*.mp4,*.mp3)","*.mp4","*.mp3"));
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("mp4 File (*.mp4)","*.mp4"),
//                new FileChooser.ExtensionFilter("mp3 File (*.mp3)","*.mp3"));
        File path = fileChooser.showOpenDialog(root.getScene().getWindow());
        if(path == null)return;
        Media media =new Media(path.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaView.setMediaPlayer(mediaPlayer);


        DoubleProperty fitWidth = mediaView.fitWidthProperty();
        fitWidth.bind(Bindings.selectDouble(mediaView.sceneProperty(),"width"));
        DoubleProperty fitHeight = mediaView.fitHeightProperty();
        fitHeight.bind(Bindings.selectDouble(mediaView.sceneProperty(),"height"));
        mediaPlayer.currentTimeProperty().addListener(new ChangeListener<Duration>() {
            @Override
            public void changed(ObservableValue<? extends Duration> observableValue, Duration duration, Duration t1) {
                sldProgress.setValue(sldProgress.getMax()*t1.toSeconds()/mediaPlayer.getTotalDuration().toSeconds());

                String fileLength = String.format("%d:%02d:%02d",(int)media.getDuration().toHours(),(int)media.getDuration().toMinutes(),
                        (int)media.getDuration().toSeconds()%60);
                lblTime.setText(String.format("%d:%02d:%02d/%s",(int)t1.toHours(),(int)t1.toSeconds()/60,(int)t1.toSeconds()%60,fileLength));
                if((int)t1.toSeconds()==(int)media.getDuration().toSeconds()){
                    btnStop.fire();
                }
            }

        });

        sldProgress.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(
                        sldProgress.getValue()*media.getDuration().toSeconds()/100-mediaPlayer.getCurrentTime().toSeconds())));
            }
        });

        if(mediaPlayer != null){
            for(Button button : new Button[]{
                    btnPlay,btnStop,btnBackword,btnForward,btnMinus,btnPlus})button.setDisable(false);
            sldProgress.setDisable(false);
            sldVolume.setDisable(false);
        }

    }
    public void btnPlayOnAction(ActionEvent actionEvent) {
        if(mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING ){
            mediaPlayer.pause();
            rateSetDefault();
            btnPlay.setText("Play");
        }
        if(mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED ||
                mediaPlayer.getStatus() == MediaPlayer.Status.READY ||
                mediaPlayer.getStatus() == MediaPlayer.Status.STOPPED){

            mediaPlayer.setRate(rate);
            mediaPlayer.play();
            btnPlay.setText("Pause");
            sldVolume.setValue(mediaPlayer.getVolume());
        }

    }
    public void rateSetDefault(){
        btnForward.setText(">>");
        btnBackword.setText("<<");
        rate = 1;
    }
    public void btnStopOnAction(ActionEvent actionEvent) {
        mediaPlayer.stop();
        btnPlay.setText("Play");
        rateSetDefault();

    }

    public void btnBackwordOnAction(ActionEvent actionEvent) {
        if(rate>=0.125)rate -= 0.125;
        mediaPlayer.setRate(rate);
        if(rate ==1)btnForward.setText(">>");
        else if (rate>1) btnForward.setText(String.format(">> %.3f",rate));
        else btnBackword.setText(String.format("<< %.3f",rate));

    }

    public void btnForwardOnAction(ActionEvent actionEvent) {
        if(rate<4)rate += 0.125;
        mediaPlayer.setRate(rate);
        if(rate ==1) btnBackword.setText("<<");
        else if(rate< 1)btnBackword.setText(String.format("<< %.3f",rate));
        else btnForward.setText(String.format(">> %.3f",rate));

    }

    public void btnMinusOnAction(ActionEvent actionEvent) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(10)));
    }

    public void btnPlusOnAction(ActionEvent actionEvent) {
        mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(10)));
    }


    public void sldProgressOnMouseDragged(MouseEvent mouseEvent) {
        mediaPlayer.pause();
        Platform.runLater(()->{
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(
                    sldProgress.getValue()*mediaPlayer.getTotalDuration().toSeconds()/sldProgress.getMax()-
                            mediaPlayer.getCurrentTime().toSeconds())));
        });
        mediaPlayer.play();
    }


    public void sldVolumeOnMouseDragged(MouseEvent mouseEvent) {
        mediaPlayer.setVolume(sldVolume.getValue());
        System.out.println(sldVolume.getValue());
    }
    public void sldVolumeOnMouseClicked(MouseEvent mouseEvent) {
        mediaPlayer.setVolume(sldVolume.getValue());
        System.out.println(sldVolume.getValue());
    }
}
