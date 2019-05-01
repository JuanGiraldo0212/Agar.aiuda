package Audio;

import java.io.File;
import java.io.IOException;
import java.net.DatagramPacket;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class MakeSound {

	public static int TAMANHOMUSICA = 3;
	public static String RUTAMUSICA = "./Musica/";
    private final int BUFFER_SIZE = 128000;
    private File soundFile;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private SourceDataLine sourceLine;
    String [] arrMusica;

    public MakeSound() {
    	arrMusica = new String [TAMANHOMUSICA];
    	arrMusica[0] = "Foster-the-People-Pumped-up-Kicks.wav";
    	arrMusica[1] = "Legends Never Die.wav";
    	arrMusica[2] = "RISE.wav";    	
    }

    public String[] getArrMusica() {
		return arrMusica;
	}

	public void setArrMusica(String[] arrMusica) {
		this.arrMusica = arrMusica;
	}

	/**
     * @param filename the name of the file that is going to be played
     */
    public void playSound(String filename){

        String strFilename = RUTAMUSICA+filename;

        try {
            soundFile = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        try {
            audioStream = AudioSystem.getAudioInputStream(soundFile);
        } catch (Exception e){
            e.printStackTrace();
            System.exit(1);
        }

        audioFormat = audioStream.getFormat();

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
        try {
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sourceLine.start();

        int nBytesRead = 0;
        byte[] abData = new byte[BUFFER_SIZE];
        while (nBytesRead != -1) {
            try {
                nBytesRead = audioStream.read(abData, 0, abData.length);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nBytesRead >= 0) {
                @SuppressWarnings("unused")
                int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
            }
        }

        sourceLine.drain();
        sourceLine.close();
    }
    
    public void detener () {
    	sourceLine.drain();
        sourceLine.close();
    }
    
    public String buscarCancion (int i) {
    	return arrMusica[i];
    }
    
    public static void main(String[] args) {
    	MakeSound m=new MakeSound();
		m.playSound("Foster-the-People-Pumped-up-Kicks.wav");
	}
}