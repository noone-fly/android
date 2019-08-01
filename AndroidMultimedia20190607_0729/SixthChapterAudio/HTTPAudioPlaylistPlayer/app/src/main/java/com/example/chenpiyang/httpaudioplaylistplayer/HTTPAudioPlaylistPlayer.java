package com.example.chenpiyang.httpaudioplaylistplayer;

import android.media.MediaPlayer;
import android.os.StrictMode;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class HTTPAudioPlaylistPlayer extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener{

    //使用向量保存播放列表的条目列表，每个条目将是一个PlaylistFile对象
    Vector playlistItems;
    Button parseButton,playButton,stopButton;
    EditText editTextUrl;
    String baseURL = "";
    MediaPlayer mediaPlayer;
    //用于跟踪目前正处于playlistItem向量的哪个条目
    int currentPlaylistItemNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_httpaudio_playlist_player);

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        //要记得初始化MediaPlayer
        mediaPlayer = new MediaPlayer();
        parseButton = (Button)this.findViewById(R.id.ButtonParse);
        playButton = (Button)this.findViewById(R.id.PlayButton);
        stopButton = (Button)this.findViewById(R.id.StopButton);

        editTextUrl = (EditText)this.findViewById(R.id.EditTextURL);
        //editTextUrl.setText("http://live.kboo.fm:8000/high.m3u");
        //editTextUrl.setText("http://ks3-cn-beijing.ksyun.com/qiubailive/record/live/252904589171420974/hls/252904589171420974-179924162492533507.m3u8");
        //editTextUrl.setText("http://pubint.ic.11nwd.net/stream/pubint_kmfa.m3u");
        //editTextUrl.setText("http://imp.qumitech.com/luPwXc_l0mxeJbv5HAeRMoGXrIZw");//java.lang.IllegalArgumentException: Illegal character in path at index 3:

        parseButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        stopButton.setOnClickListener(this);
        //起初不启用这两个button，
        playButton.setEnabled(false);
        stopButton.setEnabled(false);
    }

    //当音频播放完，触发该方法，
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        Log.v("on complete","called");
        mediaPlayer.stop();
        mediaPlayer.reset();
        if (playlistItems.size() > currentPlaylistItemNumber + 1){
            currentPlaylistItemNumber++;
            String path = ((PlaylistFile)playlistItems.get(currentPlaylistItemNumber)).getFilePath();
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //缓冲加载好了音频，触发MediaPlayer对象开始播放
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        stopButton.setEnabled(true);
        Log.v("http audio playlist","Playing");
        mediaPlayer.start();
    }

    //三个button的点击事件
    @Override
    public void onClick(View view) {
        if (view == parseButton){
            parsePlaylistFile();
        }else if (view == playButton){
            playPlaylistItems();
        }else if (view == stopButton){
            stop();
        }
    }

    //第一个被触发，该方法下载有editTextUrl对象中的URL指定的m3u文件，并进行分析，
    //分析的操作是选出任何表示待播放文件的行，创建一个PlaylistItem对象，并添加到playlistItem
    private void parsePlaylistFile(){
        playlistItems = new Vector();
        //
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet getRequest = new HttpGet(editTextUrl.getText().toString());
        Log.v("URI", getRequest.getURI().toString());
        try {
            HttpResponse httpResponse = httpClient.execute(getRequest);
            if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK){
                Log.v("http error", httpResponse.getStatusLine().getReasonPhrase());
            } else {
                InputStream inputStream = httpResponse.getEntity().getContent();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    Log.v("play list line","orig: " + line);
                    if (line.startsWith("#")){
                        //元数据
                        //可以做更多处理，暂时忽略
                    }else if (line.length() > 0){
                        String filePath = "";
                        if (line.startsWith("http://")){
                            //假设是一个完整的url
                            filePath = line;
                        }else{
                            //假设是相对url
                            filePath = getRequest.getURI().resolve(line).toString();
                            Log.v("parsePlaylistFile","filePath: " + filePath);
                        }
                        //然后，将它添加到播放列表条目的向量里
                        PlaylistFile playlistFile = new PlaylistFile(filePath);
                        playlistItems.add(playlistFile);
                    }
                }
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //最后，完成了文件的分析，启用playButton
        playButton.setEnabled(true);
    }

    //当用户点击playButton，调用playPlaylistItems方法，该方法接收playlistItems向量的第一个条目，并把它交给MediaPlayer对象
    private void playPlaylistItems(){
        playButton.setEnabled(false);
        currentPlaylistItemNumber = 0;
        if (playlistItems.size() > 0){
            String path = ((PlaylistFile)playlistItems.get(currentPlaylistItemNumber)).getFilePath();
            Log.v("playPlaylistItems","path: " + path);
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //点击stopbutton 调用stop方法，导致mediaplayer暂停而不是停止，
    private void stop(){
        mediaPlayer.pause();
        playButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    //最后，内部类，m3u文件中表示每个文件创建一个PlaylistFile对象
    class PlaylistFile{
        String filePath;
        public PlaylistFile(String _filePath){
            filePath = _filePath;
        }
        public void setFilePath(String _filePath){
            filePath = _filePath;
        }
        public String getFilePath(){
            return filePath;
        }
    }
}
