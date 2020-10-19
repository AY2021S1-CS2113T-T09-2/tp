package seedu.duke.parser;

import seedu.duke.command.Command;
import seedu.duke.command.IncorrectCommand;
import seedu.duke.command.timetable.TimeTableAddCommand;
import seedu.duke.command.timetable.TimeTableViewCommand;
import seedu.duke.data.Lesson;
import seedu.duke.data.ModuleManager;
import seedu.duke.data.TimeTableType;
import seedu.duke.exception.LessonInvalidTimeException;
import seedu.duke.ui.TextUi;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static seedu.duke.data.TimeTableType.DAY;
import static seedu.duke.data.TimeTableType.WEEK;
import static seedu.duke.util.Message.MESSAGE_CHECK_COMMAND_FORMAT;
import static seedu.duke.util.Message.MESSAGE_INVALID_COMMAND_FORMAT;

public abstract class TimeTableCommandParser {
    private static final String REPEAT_GROUP = "repeat";
    private static final Pattern TIMETABLE_VIEW_FORMAT = Pattern.compile("(?<commandFlag>-\\S+)");
    private static final Pattern TIMETABLE_ADD_FORMAT = Pattern.compile("(?<commandFlag>-add\\s*)");
    private static final Pattern TIMETABLE_LESSON_PARAMETER_FORMAT =
            Pattern.compile("(?<module>\\S+\\s*)(?<day>\\S+\\s*)(?<start>\\d+\\s*)(?<end>\\d+\\s*)(?<type>\\S+\\s*)(?<repeat>\\d+\\s*)");

    /**
     * Parses the timetable view command.
     *
     * @param parameters User input after finding that it is a view command.
     * @return TimeTableViewCommand or IncorrectCommand
     */
    public static Command parseTimeTableViewCommand(String parameters) {
        Matcher matcher = TIMETABLE_VIEW_FORMAT.matcher(parameters);
        if (!matcher.matches()) {
            return new IncorrectCommand(String.format("%s%s\n\n%s%s\n",
                    MESSAGE_INVALID_COMMAND_FORMAT, parameters, MESSAGE_CHECK_COMMAND_FORMAT, TimeTableViewCommand.FORMAT));
        }
        String commandFlag = Parser.isMatcherNull(matcher.group(Parser.COMMAND_FLAG_GROUP))
                ? null : matcher.group(Parser.COMMAND_FLAG_GROUP).toLowerCase().trim();
        TimeTableType typeOfTimeTable;
        switch (commandFlag) {
        case "-day":
            typeOfTimeTable = DAY;
            break;
        case "-week":
            typeOfTimeTable = WEEK;
            break;
        default:
            int days = Integer.parseInt(commandFlag.substring(1));
            return new TimeTableViewCommand(days);
        }
        return new TimeTableViewCommand(typeOfTimeTable);
    }

    /**
     * Parses the timetable add command. For the adding of Lessons into the timetable.
     *
     * @param parameters User input after finding that it is an add command.
     * @return TimeTableAddCommand or IncorrectCommand
     * @throws ModuleManager.ModuleNotFoundException When the module is not found in the module list.
     * @throws LessonInvalidTimeException When the start is greater than or equal to end time of the Lesson.
     * @throws DateTimeParseException When the time of either the start or end is in the wrong format.
     */
    public static Command parseTimeTableAddCommand(String parameters)
            throws ModuleManager.ModuleNotFoundException, LessonInvalidTimeException, DateTimeParseException {
        Matcher addMatcher = TIMETABLE_ADD_FORMAT.matcher(parameters);
        if (!addMatcher.matches()) {
            return new IncorrectCommand(String.format("%s%s\n\n%s%s\n",
                    MESSAGE_INVALID_COMMAND_FORMAT, parameters, MESSAGE_CHECK_COMMAND_FORMAT, TimeTableAddCommand.FORMAT));
        }
        String lessonParams = TextUi.getLessonParams();
        Matcher lessonMatcher = TIMETABLE_LESSON_PARAMETER_FORMAT.matcher(lessonParams);
        if (!lessonMatcher.matches()) {
            return new IncorrectCommand(String.format("%s%s\n\n%s%s\n",
                    MESSAGE_INVALID_COMMAND_FORMAT, lessonParams, MESSAGE_CHECK_COMMAND_FORMAT, TIMETABLE_LESSON_PARAMETER_FORMAT));
        }
        Lesson newLesson = LessonParser.parseLesson(lessonMatcher);
        // Convert repeatString to int
        String repeatString = lessonMatcher.group(REPEAT_GROUP).toLowerCase().trim();
        int repeatFreq = Integer.parseInt(repeatString);
        // Get current week num
        LocalDateTime now = LocalDateTime.now();
        int currWeekNum = now.get(ChronoField.ALIGNED_WEEK_OF_YEAR);
        return new TimeTableAddCommand(newLesson, currWeekNum, repeatFreq);
    }
}