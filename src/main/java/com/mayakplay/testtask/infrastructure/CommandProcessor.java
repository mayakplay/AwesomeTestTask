package com.mayakplay.testtask.infrastructure;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mayakplay.testtask.adapter.LocalDateTypeAdapter;
import com.mayakplay.testtask.annotation.CommandDescription;
import com.mayakplay.testtask.annotation.CommandMethod;
import com.mayakplay.testtask.exception.CommandAlreadyExistsException;
import com.mayakplay.testtask.exception.InvalidCommandNameException;
import com.mayakplay.testtask.type.ArgumentMistakeType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.validation.*;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс используется для регистрации и обработки команд
 */
public final class CommandProcessor {

    private final Gson gson;
    private final Validator validator;

    /**
     * Мапа содержит в себе зарегистрированные определения команд
     *
     * &lt;Имя команды, Определение команды&gt;
     */
    private final Map<String, CommandDefinition> classDefinitionsMap = new LinkedHashMap<>();

    //region CONSTRUCTION
    public CommandProcessor() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateTypeAdapter("dd.MM.u"))
                .create();

        registerProcessedController(this);

        final ValidatorFactory factory = Validation.byDefaultProvider()
                .configure()
                .buildValidatorFactory();

        this.validator = factory.getValidator();
    }

    /**
     * Метод ищет методы, над которыми стоит {@link CommandMethod},
     * в классе объекта контроллера и создает из них {@link CommandDefinition}
     */
    public void registerProcessedController(Object controller) {
        for (Method method : controller.getClass().getDeclaredMethods()) {
            final CommandMethod annotation = method.getAnnotation(CommandMethod.class);

            if (annotation != null) {
                final String commandName = annotation.value().toUpperCase();

                if (commandName.contains(" ")) {
                    throw new InvalidCommandNameException();
                }

                if (!classDefinitionsMap.containsKey(commandName)) {
                    classDefinitionsMap.put(commandName, CommandDefinition.of(controller, method));
                } else {
                    throw new CommandAlreadyExistsException();
                }
            }
        }
    }

    public void startProcessing() {
        printHelp();
    }
    //endregion

    //region PROCESSING

    /**
     * Метод обрабатывает строку, как команду
     *
     * @param commandLine строка команды
     * @return вывод команды
     */
    @NotNull
    public String processCommand(@NotNull String commandLine) {
        System.out.println(commandLine);
        String[] split = commandLine.split(" ");

        CommandDefinition commandDefinition = classDefinitionsMap.get(split[0].toUpperCase());

        String UNKNOWN_COMMAND_MESSAGE = "Unknown command! Try \'?\', to get all commands.";
        if (commandDefinition == null)
            return UNKNOWN_COMMAND_MESSAGE;

        final List<ArgumentProcessingDescription> processingDescriptions = processArguments(
                commandDefinition, Arrays.copyOfRange(split, 1, split.length));

        final boolean successArgumentsProcessing = processingDescriptions.stream()
                .map(ArgumentProcessingDescription::getMistakeType)
                .allMatch(argumentMistakeType -> argumentMistakeType.equals(ArgumentMistakeType.OK));

        if (successArgumentsProcessing) {
            final Object[] invocationObjects = processingDescriptions.stream()
                    .map(ArgumentProcessingDescription::getObject)
                    .toArray();

            return processInvocation(commandDefinition, invocationObjects);
        }

        processingDescriptions.stream()
                .filter(processingDescription -> !processingDescription.getMistakeType().equals(ArgumentMistakeType.OK))
                .map(ArgumentProcessingDescription::getErrorDescription)
                .forEach(System.out::println);
        return "ERROR";
    }

    /**
     * Метод преобразует массив строк аргументов в лист {@link ArgumentProcessingDescription},
     * содержащий информацию об обработке аргументов.
     * <p>
     * Размер выходящего листа, ВСЕГДА, будет равен количеству необходимых
     * команде аргументов.
     *
     * @param commandDefinition определение команды, для которой необходимо подать аргументы
     * @param commandLineArgs   массив строк
     * @return лист обработаных аргументов, для определения команды,
     * полученный из массива строк
     */
    @NotNull
    private List<ArgumentProcessingDescription> processArguments(
            @NotNull CommandDefinition commandDefinition,
            @NotNull String[] commandLineArgs
    ) {
        final List<ArgumentDefinition> argumentsList = commandDefinition.getArgumentsList();
        final List<ArgumentProcessingDescription> processingDescriptionsList = new ArrayList<>();

        for (int i = 0; i < argumentsList.size(); i++) {
            final ArgumentDefinition argumentDefinition = argumentsList.get(i);
            final @Nullable String argumentString = commandLineArgs.length > i ? commandLineArgs[i] : null;

            processingDescriptionsList.add(processArgument(argumentDefinition, argumentString));
        }

        return processingDescriptionsList;
    }

    /**
     * Метод обрабатывает конкретную строку (слово), для получения описания
     * обработки аргумента
     *
     * @param argumentDefinition определение аргумента
     * @param argumentString     строка аргумента
     * @return вернет описание обработки строки аргумента
     */
    private ArgumentProcessingDescription processArgument(
            @NotNull ArgumentDefinition argumentDefinition,
            @Nullable String argumentString
    ) {
        if (argumentString == null)
            return ArgumentProcessingDescription.notSpecified(argumentDefinition.getName());

        try {
            final Object parsedObject = gson.fromJson(argumentString, argumentDefinition.getType());

            return ArgumentProcessingDescription.ok(parsedObject);
        } catch (Exception e) {
            final String errorDescription = !Strings.isNullOrEmpty(argumentDefinition.getErrorDescription())
                    ? argumentDefinition.getErrorDescription() : e.getMessage();

            return ArgumentProcessingDescription.error(" " + argumentDefinition.getName() + ": " + errorDescription);
        }
    }

    /**
     * Запустит метод объекта контроллера из определения команды
     *
     * @param commandDefinition определение команды, которую необходимо запустить
     * @param argumentObjects   массив обработанных аргументов
     * @return результат, полученный при запуске метода.
     * "OK", если результат - null
     * "ERROR", если произошла ошибка, в ходе исполнения
     */
    @NotNull
    private String processInvocation(@NotNull CommandDefinition commandDefinition, @NotNull Object[] argumentObjects) {
        final Method commandMethod = commandDefinition.getCommandMethod();

        commandMethod.setAccessible(true);
        try {
            validateArguments(commandDefinition, argumentObjects);

            final Object invoke = commandMethod.invoke(commandDefinition.getController(), argumentObjects);

            return invoke == null ? "OK" : invoke.toString();
        } catch (ReflectiveOperationException | ValidationException ignored) {
        }

        return "ERROR";
    }

    /**
     * Метод проверяет входящие параметры команды,
     * печатает неправильно введенные параметры, если они есть.
     *
     * @param commandDefinition определение команды
     * @param argumentObjects   массив параметро
     *
     * @throws ValidationException если есть неправильно введенные параметры
     */
    private void validateArguments(
            @NotNull CommandDefinition commandDefinition,
            @NotNull Object[] argumentObjects
    ) throws ValidationException {
        Set<ConstraintViolation<@NotNull Object>> constraintViolations = validator.forExecutables().validateParameters(
                commandDefinition.getController(),
                commandDefinition.getCommandMethod(),
                argumentObjects
        );

        for (ArgumentDefinition argumentDefinition : commandDefinition.getArgumentsList()) {
            for (ConstraintViolation<Object> violation : constraintViolations) {
                final String argumentPropertyPath = commandDefinition.getCommandMethod().getName() + "."
                        + argumentDefinition.getOriginalParameterName();

                if (argumentPropertyPath.equals(violation.getPropertyPath().toString())) {
                    System.out.println(" " + argumentDefinition.getName() + ": " + violation.getMessage());
                }
            }
        }

        if (constraintViolations.size() > 0) throw new ValidationException();
    }

    //endregion

    //region DEFAULT COMMANDS

    /**
     * Метод стандартной команды выхода из программы
     */
    @CommandMethod("Q")
    @CommandDescription("To quit")
    private void exitCommandMethod() {
        System.out.println(1);
        System.exit(0);
    }

    /**
     * Метод стандартной команды "?", печатающий
     * описание существующих команд
     */
    @CommandMethod("?")
    @CommandDescription("Prints this list")
    private String helpCommandMethod() {
        System.out.println("Available commands:");
        printHelp();
        System.out.println("\"q\" to quit");
        return "";
    }

    /**
     * Метод печатает доступные команды и их описание
     */
    private void printHelp() {
        for (Map.Entry<String, CommandDefinition> entry : classDefinitionsMap.entrySet()) {
            final String commandName = entry.getKey();
            final CommandDefinition commandDefinition = entry.getValue();

            final String argumentsString = commandDefinition.getArgumentsList().stream()
                    .map(ArgumentDefinition::getName)
                    .collect(Collectors.joining(", "));

            final String commandMessage = String.format(" %s |%s| - %s",
                    commandName.toUpperCase(),
                    argumentsString,
                    commandDefinition.getDescription());

            System.out.println(commandMessage);
        }
    }
    //endregion

}