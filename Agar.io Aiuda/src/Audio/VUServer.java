package Audio;

import java.io.*;
import javax.sound.sampled.*;
import java.io.File;
import java.nio.file.Files;

public class VUServer {

ByteArrayOutputStream byteOutputStream;
AudioInputStream InputStream;
SourceDataLine sourceLine;
AudioFormat audioFormat;

public static void main(String args[]) {
    new VUServer().runVOIP();
}

public void runVOIP() {
    try {
        while (true) {
            try {
            	File file = new File("./Musica/Foster-the-People-Pumped-up-Kicks.wav");
                byte audioData[] = Files.readAllBytes(file.toPath());
                //InputStream byteInputStream = new ByteArrayInputStream(audioData);
                InputStream =  AudioSystem.getAudioInputStream(file);
               // audioFormat = audioStream.getFormat();
                audioFormat = getAudioFormat();
                
                //InputStream = new AudioInputStream(byteInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());
                DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
                sourceLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                sourceLine.open(audioFormat);
                sourceLine.start();
                Thread playThread = new Thread(new PlayThread());
                playThread.start();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

private AudioFormat getAudioFormat() {
    float sampleRate = 44100.0F;
    int sampleInbits = 16;
    int channels = 2;
    boolean signed = true;
    boolean bigEndian = false;
    return new AudioFormat(sampleRate, sampleInbits, channels, signed, bigEndian);
}

class PlayThread extends Thread {

    byte tempBuffer[] = new byte[10000];

    public void run() {
        try {
            int cnt;
            while ((cnt = InputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                if (cnt > 0) {
                    sourceLine.write(tempBuffer, 0, cnt);
                }
            }
            //  sourceLine.drain();
            // sourceLine.close();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }
}
}