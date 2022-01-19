package put.edu.gui.serverapi;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public class Reader extends ServerCommunicator {
    private static final String messageClassesPackage = "put.edu.gui.game.messages";
    private static final List<Class<?>> classList = findAllClassesUsingClassLoader();
    private final BufferedReader bufferedReader;

    public Reader(InputStream inputStream) {
        this.bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    }

    private static List<Class<?>> findAllClassesUsingClassLoader() {
        InputStream stream = ClassLoader.getSystemClassLoader()
                .getResourceAsStream(Reader.messageClassesPackage.replaceAll("[.]", "/"));
        BufferedReader reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(stream)));
        return reader.lines()
                .filter(line -> line.endsWith(".class"))
                .map(Reader::getClass)
                .collect(Collectors.toList());
    }

    private static Class<?> getClass(String className) {
        try {
            return Class.forName(Reader.messageClassesPackage + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException e) {
            log.error("cannot find class: " + className);
        }
        return null;
    }

    @Override
    public void run() {
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(System.lineSeparator());
            }
            Optional<Message> optionalMessage = convertToMessage(stringBuilder.toString());
            getMessageQueue().add(optionalMessage.orElseThrow());

        } catch (IOException e) {
            log.error("Received invalid message");
        }
    }

    private Optional<Message> convertToMessage(String string) {
        Message message = null;
        for (Class<?> messageClass : classList) {
            try {
                message = (Message) new Gson().fromJson(string, messageClass);
            } catch (JsonSyntaxException ignored) {
            }
        }
        return Optional.ofNullable(message);
    }

}
