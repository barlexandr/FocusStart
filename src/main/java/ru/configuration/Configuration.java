package ru.configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

public class Configuration {
    private DataType dataType = null;           // Тип входных данных string или int
    private boolean error = false;              // Переменная-флаг для обозначения ошибки
    private boolean sortByAscending = true;     // Сортировка по умолчанию: по возрастанию
    private int numberOfOutputInStringArray = Integer.MAX_VALUE; // Порядковый номер выходного файла среди всех полученных аргументов
    private final String dir = System.getProperty("user.dir");   // Путь до папки с проектом
    private boolean flagStr = false;
    private boolean flagInt = false;

    private final File outFile;                // Выходной файл
    private final List<File> inputFileList = new ArrayList<>();    // Входные файлы

    public Configuration(String[] args) throws Exception {
        // Обязательно должено быть указано:
        // 1) Тип данных
        // 2) Имя выходного файла
        // 3) Хотя бы 1 входной файл
        // Если указано --help, то выводим информацию и ожидаем ввода выражения по enter, затем обновляем его
        if (args.length < 3) {
            if (args.length != 0) {
                if (args[0].equals("--help")) {
                    args = Help.showHelp();
                }
            }else
                throw new Exception("Вы обязательно должны ввести тип данных, имя выходного и хотя бы 1 входного файла." +
                        "\n --help для вызова меню.");
        }

        for(int i = 0; i < args.length; i++){
            switch (args[i]){
                case "-a":
                    break;
                case "-d":
                    sortByAscending = false;
                    break;
                case "-s":
                    dataType = DataType.STRING;
                    break;
                case "-i":
                    dataType = DataType.INTEGER;
                    break;
                default:
                    boolean p = Pattern.matches(".*out.*.txt$", args[i]);
                    if (p)
                        numberOfOutputInStringArray = i;
            }
        }

        // Если ввели тип данных стринг и инт одновременно, то ошибка
        Iterator<String> iteratorString = Arrays.stream(args).iterator();
        while (iteratorString.hasNext()){
            String nextEl = iteratorString.next();
            if (nextEl.equals("-s"))
                flagStr = true;

            if (nextEl.equals("-i"))
                flagInt = true;

            if (flagInt && flagStr)
                throw new Exception("Ошибка при вводе типа данных. Введите -s, либо -i. \n --help для вызова меню.");

        }

        // Если порядковый номер выходного файла больше или равен количеству элементов, значит входные файлы не указаны.
        if(numberOfOutputInStringArray >= args.length - 1)
            throw new Exception("""
                    Ошибка входных данных.\s
                     Проверьте порядок ввода аргументов и колличество входных файлов.
                     --help для вызова меню.""");
        else {
            if (sortByAscending || !sortByAscending && args[numberOfOutputInStringArray].toLowerCase().contains("rev"))
                outFile = new File(dir + "\\" + args[numberOfOutputInStringArray]); // Присваиваем
            else {
                String[] splitPath = args[numberOfOutputInStringArray].split("\\.txt");
                outFile = new File(dir + "\\" + splitPath[0] + "Rev.txt");
            }

            if(!outFile.isFile())
                outFile.createNewFile();

            // Проходим по всем входным файлам и добавляем их в список.
            for(int i = numberOfOutputInStringArray + 1; i < args.length; i++){
                File inputFile = new File(dir + "\\" + args[i]);
                if (!inputFile.isFile()){
                    throw new Exception("Проверьте наличие входных файлов и правильность их написания в консоли." +
                            "\n --help для вызова меню.");
                }
                inputFileList.add(inputFile);
            }
        }
    }

    public DataType getDataType() {
        return dataType;
    }

    public boolean isSortByAscending() {
        return sortByAscending;
    }

    public int getNumberOfOutputInStringArray() {
        return numberOfOutputInStringArray;
    }

    public String getDir() {
        return dir;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public boolean isError() {
        return error;
    }

    public File getOutFile() {
        return outFile;
    }

    public List<File> getInputFileList() {
        return inputFileList;
    }
}
