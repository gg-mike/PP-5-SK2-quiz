package put.edu.gui.serverapi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.rxjava3.subjects.UnicastSubject;
import put.edu.gui.game.messages.Message;
import put.edu.gui.game.messages.MessageTypePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public class Reader extends ServerCommunicator {
    private static final String messageClassesPackage = "put.edu.gui.game.messages.responses";
    private static final String BEGIN_MESSAGE = "<$begin$>";
    private static final String END_MESSAGE = "<$end$>";
    private final BufferedReader bufferedReader;

    public Reader(InputStream inputStream) {
        super(UnicastSubject.create());
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        this.start();
    }

    private static Class<?> getClassByName(String className) {
        try {
            return Class.forName(Reader.messageClassesPackage + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            System.err.println("cannot find class: " + className);
        }
        return null;
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
                System.out.println("Read json: \n" + jsonString);
                Optional<Message> optionalMessage = convertToMessage(jsonString);
                Message message = optionalMessage.orElseThrow();
                System.out.println("Message read: " + message);
                getMessageSubject().onNext(message);
            } catch (Exception e) {
                System.out.println("Received invalid message");
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

    private Optional<Message> convertToMessage(String jsonString) {
        Gson gson = new Gson();
        Type empMapType = new TypeToken<Map<String, Object>>() {
        }.getType();
        Optional<Map<String, Object>> optionalJsonMap = Optional.ofNullable(gson.fromJson(jsonString, empMapType));
        if (optionalJsonMap.isEmpty() || !optionalJsonMap.get().containsKey("type") ||
                !(optionalJsonMap.get().get("type") instanceof Integer)) {
            return Optional.empty();
        }
        int messageType = (int) optionalJsonMap.get().get("type");
        for (MessageTypePair messageTypePair : MessageTypePair.values()) {
            if (messageTypePair.getMessageType() == messageType) {
                Message message = (Message) new Gson().fromJson(jsonString, getClassByName(messageTypePair.toString()));
                return Optional.of(message);
            }
        }
        return Optional.empty();
    }

}
