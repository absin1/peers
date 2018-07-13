/**
 * 
 */
package net.sourceforge.peers.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

import com.google.cloud.texttospeech.v1beta1.AudioConfig;
import com.google.cloud.texttospeech.v1beta1.AudioEncoding;
import com.google.cloud.texttospeech.v1beta1.SsmlVoiceGender;
import com.google.cloud.texttospeech.v1beta1.SynthesisInput;
import com.google.cloud.texttospeech.v1beta1.SynthesizeSpeechResponse;
import com.google.cloud.texttospeech.v1beta1.TextToSpeechClient;
import com.google.cloud.texttospeech.v1beta1.VoiceSelectionParams;
import com.google.protobuf.ByteString;

import net.sourceforge.peers.media.AbstractSoundManager;

/**
 * @author absin
 *
 */
public class StreamSoundManager extends AbstractSoundManager {

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.peers.media.SoundSource#readData()
	 */
	// @Override
	public byte[] readData1() {
		String text = "Hello World! How are you doing today? This is Google Cloud Text-to-Speech Demo!";

		try (TextToSpeechClient textToSpeechClient = TextToSpeechClient.create()) {
			// Set the text input to be synthesized
			SynthesisInput input = SynthesisInput.newBuilder().setText(text).build();

			// Build the voice request; languageCode = "en_us"
			VoiceSelectionParams voice = VoiceSelectionParams.newBuilder().setLanguageCode("en-US")
					.setSsmlGender(SsmlVoiceGender.MALE).build();

			// Select the type of audio file you want returned
			AudioConfig audioConfig = AudioConfig.newBuilder().setAudioEncoding(AudioEncoding.LINEAR16)
					.setSampleRateHertz(8000) // MP3 audio.
					.build();
			// Perform the text-to-speech request
			SynthesizeSpeechResponse response = textToSpeechClient.synthesizeSpeech(input, voice, audioConfig);

			// Get the audio contents from the response
			ByteString audioContents = response.getAudioContent();
			try (OutputStream out = new FileOutputStream("C:\\Users\\absin\\Downloads\\output1.wav")) {
				out.write(audioContents.toByteArray());
				System.out.println("Audio content written to file \"output.mp3\"");
			}
			System.err.println("!!!!!!!!!!!!!!!!YYAAAAYYYYYYYYYYYYYY!!!!!");
			return audioContents.toByteArray();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println("!!!!!!!!!!!!!!!!NAAAAAAAAAAAAAAAAAAAAAAAAA!!!!!");
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.peers.media.SoundSource#readData()
	 */
	@Override
	public byte[] readData() {
		File file = new File("C:\\Users\\absin\\Downloads\\output1.wav");
		FileInputStream fin = null;
		byte[] byteArray = null;
		try {
			// create FileInputStream object
			fin = new FileInputStream(file);

			// create string from byte array
			byteArray = IOUtils.toByteArray(fin);

		} catch (FileNotFoundException e) {
			System.err.println("File not found" + e);
		} catch (IOException ioe) {
			System.err.println("Exception while reading file " + ioe);
		} finally {
			// close the streams using close method
			try {
				if (fin != null) {
					fin.close();
				}
			} catch (IOException ioe) {
				System.err.println("Error while closing stream: " + ioe);
			}
		}
		System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		System.err.println(byteArray.length + "byteArray");
		return byteArray;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.peers.media.AbstractSoundManager#init()
	 */
	@Override
	public void init() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.peers.media.AbstractSoundManager#close()
	 */
	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.sourceforge.peers.media.AbstractSoundManager#writeData(byte[], int,
	 * int)
	 */
	@Override
	public int writeData(byte[] buffer, int offset, int length) {
		// TODO Auto-generated method stub
		return 0;
	}

}
