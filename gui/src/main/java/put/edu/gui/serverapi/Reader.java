package put.edu.gui.serverapi;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.reactivex.rxjava3.subjects.UnicastSubject;
import put.edu.gui.game.messages.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public class Reader extends ServerCommunicator {
    private final BufferedReader bufferedReader;

    public Reader(InputStream inputStream) {
        super(UnicastSubject.create());
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 1);
    }

    @Override
    public void run() {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(System.lineSeparator());
            }
            System.out.println("readed: " + stringBuilder.toString());
            Optional<Message> optionalMessage = convertToMessage(stringBuilder.toString());
            Message message = optionalMessage.orElseThrow();
            System.out.println("Message read: " + message);
//            getMessageSubject().onNext(message);
        } catch (IOException e) {
            System.out.println("Received invalid message");
        }
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
//        for (MessageTypePair messageTypePair: MessageTypePair.values()) {
//            if (messageTypePair.getMessageType() == messageType) {
////                Message message = new Gson().fromJson()
//            }
//        }
        return Optional.empty();
    }

}
