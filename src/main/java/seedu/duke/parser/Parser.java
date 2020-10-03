package seedu.duke.parser;

public class Parser {
    public static String parseCommand(String userInput){
        switch (userInput){
        case "add":
            return addCommand(userInput);

        case "delete":
            return deleteCommand(userInput);

        case "list":
            return listCommand(userInput);

        case "edit":
            return editCommand(userInput);

        case "done":
            return doneCommand(userInput);

        case "bye":
            return byeCommand(userInput);

        default:
            return HelpCommand();
        }
    }
}
