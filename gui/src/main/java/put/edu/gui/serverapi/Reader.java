package put.edu.gui.serverapi;

import com.google.gson.Gson;
import io.reactivex.rxjava3.subjects.PublishSubject;
import put.edu.gui.KahootApp;
import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageTypePair;
import put.edu.gui.game.messages.responses.ResponseMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

public class Reader extends ServerCommunicator {
    private static final String messageClassesPackage = "put.edu.gui.game.messages.responses";
    private final BufferedReader bufferedReader;

    public Reader(InputStream inputStream) {
        super(PublishSubject.create());
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.start();
    }

    private static Class<?> getClassByName(String className) throws ClassNotFoundException {
        return Class.forName(Reader.messageClassesPackage + "." + className);
    }

    @Override
    public void run() {
        String jsonString;
        while (isRunning()) {
            try {
                while ((jsonString = bufferedReader.readLine()) != null) {
                    if (jsonString.startsWith(BEGIN_MESSAGE)) {
                        jsonString = jsonString.substring(BEGIN_MESSAGE.length());
                        jsonString = readBuffer(jsonString);
                        break;
                    }
                }
                if (jsonString == null) {
                    throw new IOException("line is null");
                }
                System.out.println("Read json: \n" + jsonString);
                Optional<Message> optionalMessage = convertToMessage(jsonString);
                Message message = optionalMessage.orElseThrow();
                System.out.println("Message read: " + message);
                getMessageSubject().onNext(message);
            } catch (ClassNotFoundException classNotFoundException) {
                System.err.println("Received invalid message cannot find message class");
            } catch (IOException ioException) {
                KahootApp.get().disconnect();
                this.stop();
            } catch (Exception e) {
                System.err.println("Received invalid message");
            }
        }
    }

    private String readBuffer(String line) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        while (!line.endsWith(END_MESSAGE)) {
            stringBuilder.append(line).append(System.lineSeparator());
            line = bufferedReader.readLine();
            if (line == null) {
                throw new IOException("line is null");
            }
        }
        line = line.substring(0, line.length() - END_MESSAGE.length());
        stringBuilder.append(line);
        return stringBuilder.toString();
    }

    private Optional<Message> convertToMessage(String jsonString) throws ClassNotFoundException {
        Message message = new Gson().fromJson(jsonString, ResponseMessage.class);
        for (MessageTypePair messageTypePair : MessageTypePair.values()) {
            if ((message.getType() & messageTypePair.getMessageType()) == messageTypePair.getMessageType()) {
                message = (Message) new Gson().fromJson(jsonString, getClassByName(messageTypePair.toString()));
                return Optional.ofNullable(message);
            }
        }
        return Optional.ofNullable(message);
    }

}
