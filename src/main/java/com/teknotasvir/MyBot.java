package com.teknotasvir;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagException;
import org.jaudiotagger.tag.images.Artwork;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class MyBot extends TelegramLongPollingBot {
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()){
            long chatId = update.getMessage().getChatId();
            if (update.getMessage().hasText()){
                switch (update.getMessage().getText()){
                    case "/start":
                        start(chatId);
                }
            } if (update.getMessage().hasAudio()){
                String fileId = update.getMessage().getAudio().getFileId();
                download(fileId, chatId);
            }
        }
    }

    private void download(String fileId, long chatId) {
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        try {
            File file = execute(getFile);
            System.out.println("downloading");
            String filePath = file.getFilePath();
            downloadAudio(filePath, chatId);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void downloadAudio(String filePath, long chatId) throws IOException, TelegramApiException {
        URL url = new URL("https://api.telegram.org/file/bot8594272810:AAGv0YMdCIkGJ3QfzoOSNFg0PZFPXr_RAek/"+filePath);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        InputStream in = urlConnection.getInputStream();
        new java.io.File("downloads").mkdirs();
        OutputStream out = new FileOutputStream("downloads/"+chatId+".mp3");

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("در حال آپلود فایل " + 0 + "%");
        Message msg = execute(sendMessage);
        int messageId = msg.getMessageId();

        byte[] buffer = new byte[1024];
        int read;
        int fileSize = urlConnection.getContentLength();
        int totalRead = 0;
        int perProgress = 0;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
            totalRead += read;
            int progress = (totalRead * 100) / fileSize;
            if (progress != perProgress){
                updateProgress(messageId, chatId, ("در حال آپلود فایل " + progress + "%"));
                perProgress = progress;
            }

        }
        System.out.println("Downloaded");
        sendMusicInfo(chatId);
    }

    private void sendMusicInfo(long chatId) {
        String photoPath;
        MusicInfoModel info = null;
        String infoStr = "";
        try {
            AudioFile audioFile = AudioFileIO.read(new java.io.File("downloads/" + chatId + ".mp3"));
            Tag tag = audioFile.getTag();
            photoPath = getCover(chatId, tag);
            info = getMusicInfo(tag);
            infoStr = info.toString();
            if (photoPath.isEmpty()){
                return;
            }
        } catch (Exception e) {
            photoPath = "thumnails/blank.png";
        }
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(new java.io.File(photoPath)));
        sendPhoto.setCaption(infoStr);
        List<List<InlineKeyboardButton>> keyboard = getKeyboard();
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(keyboard);
        sendPhoto.setReplyMarkup(markup);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private MusicInfoModel getMusicInfo(Tag tag) {
        return  new MusicInfoModel(
                tag.getFirst(FieldKey.TITLE),
                tag.getFirst(FieldKey.ARTIST),
                tag.getFirst(FieldKey.ALBUM),
                tag.getFirst(FieldKey.GENRE),
                tag.getFirst(FieldKey.YEAR)
        );
    }

    private List<List<InlineKeyboardButton>> getKeyboard() {
        InlineKeyboardButton changeTitleButton = new InlineKeyboardButton();
        changeTitleButton.setText("تغییر نام");
        changeTitleButton.setCallbackData("changeTitleButton");
        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(changeTitleButton);
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        keyboard.add(row1);
        return keyboard;
    }

    private String getCover(long chatId, Tag tag) {
        String thumnailPath = "thumnails/" + chatId + ".jpg";
        if (tag != null){
            Artwork artwork = tag.getFirstArtwork();
            if (artwork != null){
                try (OutputStream os = new FileOutputStream(thumnailPath)) {
                    byte[] img = artwork.getBinaryData();
                    os.write(img);
                    return thumnailPath;
                } catch (IOException e) {
                    return "thumnails/blank.png";
                }
            }
        }
        return "thumnails/blank.png";
    }

    private void updateProgress(int messageId, long chatId, String text) throws TelegramApiException {
        EditMessageText edit = new EditMessageText();
        edit.setChatId(chatId);
        edit.setMessageId(messageId);
        edit.setText(text);
        execute(edit);
    }

    private void start(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("برای شروع آهنگ مورد نظر خود را آپلود یا فوروارد کنید!");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "@mustof_test_bot";
    }

    @Override
    public String getBotToken() {
        return "8594272810:AAGv0YMdCIkGJ3QfzoOSNFg0PZFPXr_RAek";
    }
}
