package com.teknotasvir;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.ArtworkFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class CoverExtractor {
    public void extract(String inputFile, String outputFile) {
        try{
            AudioFile audioFile = AudioFileIO.read(new File(inputFile));
            Tag tag = audioFile.getTag();
            if (tag != null){
                Artwork artwork = tag.getFirstArtwork();
                if (artwork != null){
                    byte[] coverDate = artwork.getBinaryData();
                    try (FileOutputStream os = new FileOutputStream(outputFile)) {
                        os.write(coverDate);
                    }
                }
            }

        }catch (IOException e){
            e.printStackTrace();
        } catch (CannotReadException e) {
            throw new RuntimeException(e);
        } catch (TagException e) {
            throw new RuntimeException(e);
        } catch (InvalidAudioFrameException e) {
            throw new RuntimeException(e);
        } catch (ReadOnlyFileException e) {
            throw new RuntimeException(e);
        }
    }

    public void setCover(String music, String cover) throws Exception {
        AudioFile audioFile = AudioFileIO.read(new File(music));
        Tag tag = audioFile.getTag();
        if (tag != null){
            Artwork artwork =
                    ArtworkFactory.createArtworkFromFile(
                            new java.io.File(cover)
                    );
            tag.deleteArtworkField();
            tag.setField(artwork);
            audioFile.commit();
        }
    }
}
