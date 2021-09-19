package ru.sort;

import ru.configuration.Configuration;
import ru.configuration.DataType;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.*;

public class MergeSort {
    Configuration configuration;
    String tempMinOrMaxElement = null;
    List<String> changeableListOfElement = new LinkedList<>(); // Лист рассматриваемых элементов по 1 из каждого файла

    public MergeSort(Configuration configuration) {
        this.configuration = configuration;
    }

    // Сортировка
    public void sort() {
        try {
            File outFile = configuration.getOutFile();
            List<File> inputFileList = configuration.getInputFileList();
            Writer writer = new OutputStreamWriter(new FileOutputStream(outFile), UTF_8);

            // Создаем лист элементов и помещаем в него по 1 элементу из каждого входного файла.
            // Если инт, то считываем NextInt, если string, то находим длину строки
            if (configuration.getDataType().equals(DataType.INTEGER)) {
                for (int i = 0; i < inputFileList.size(); i++) {
                    Scanner scannerForEveryInputFile = new Scanner(inputFileList.get(i));
                    if (scannerForEveryInputFile.hasNextInt())
                        changeableListOfElement.add(String.valueOf(scannerForEveryInputFile.nextInt()));
                    else
                        inputFileList.remove(i);
                }
                // Если тип данных стринг, то добавляем в список сами строки
            } else if (configuration.getDataType().equals(DataType.STRING)){
                for (int i = 0; i < inputFileList.size(); i++) {
                    Scanner scannerForEveryInputFile = new Scanner(inputFileList.get(i));
                    if (scannerForEveryInputFile.hasNextLine())
                        changeableListOfElement.add(String.valueOf(scannerForEveryInputFile.nextLine()));
                    else
                        inputFileList.remove(i);

                    scannerForEveryInputFile.close();
                }
                // Если не указан тип данных, выводим сообщение об ошибке
            } else{
                throw new Error("Не указан тип данных. Проверьте ввод консоли на правильность." +
                        "\n --help для вызова меню.");
            }

            // Пока лист входных файлов не пуст.
            while (!inputFileList.isEmpty()) {
                int indexOfMinOrMaxTempElement;

                // Если входной тип - инт, то сравниваем значения
                if (configuration.getDataType().equals(DataType.INTEGER)) {
                    if (configuration.isSortByAscending())
                        tempMinOrMaxElement = String.valueOf(Collections.min(changeableListOfElement.stream().map(Integer::valueOf).collect(Collectors.toList())));
                    else
                        tempMinOrMaxElement = String.valueOf(Collections.max(changeableListOfElement.stream().map(Integer::valueOf).collect(Collectors.toList())));
                    indexOfMinOrMaxTempElement = changeableListOfElement.indexOf(String.valueOf(tempMinOrMaxElement));
                }
                // Если входной тип - стринг, сравниваем длины строк
                else if (configuration.getDataType().equals(DataType.STRING)){
                    if (configuration.isSortByAscending())
                        tempMinOrMaxElement = Collections.min(changeableListOfElement, Comparator.comparing(String::length));
                    else
                        tempMinOrMaxElement = Collections.max(changeableListOfElement, Comparator.comparing(String::length));
                    indexOfMinOrMaxTempElement = changeableListOfElement.indexOf(String.valueOf(tempMinOrMaxElement));
                }
                else
                    throw new Exception("Проверьте выбранный тип данных на корректность." +
                            "\n --help для вызова меню.");

                writer.write(tempMinOrMaxElement + "\n"); // Записываем в выходной файл  элемент
                writer.flush();

                // Создаем сканнер для чтения файла с мин / макс элементом и удаляем этот элемент из списка элементов.
                Scanner tempScanner = new Scanner(inputFileList.get(indexOfMinOrMaxTempElement), Charset.forName(UTF_8.name()));
                changeableListOfElement.remove(indexOfMinOrMaxTempElement);

                String searchStr = String.valueOf(tempMinOrMaxElement);
                while (true){
                    // Если следующий элемент тот, который мы удалили, переходим на следующую линию
                    if (tempScanner.hasNext(Pattern.quote(searchStr))) {
                        tempScanner.nextLine();
                        break;
                    }

                    // Если элемент последний или следующая строка совпадает с удаленной, то выходим
                    if (!tempScanner.hasNextLine() || tempScanner.nextLine().equals(searchStr)){
                        break;
                    }
                }

                // Если в файле содержится следующая строка, то добавляем ее в список элементов
                // и сравниваем с предыдущим мин/макс элементом для определения правильности отсортированного файла.
                // Если не содержит, то удаляем файл из рассмотрения.
                if (tempScanner.hasNextLine()) {
                    changeableListOfElement.add(indexOfMinOrMaxTempElement, String.valueOf(tempScanner.nextLine()));
                    String changeableElement = changeableListOfElement.get(indexOfMinOrMaxTempElement); // Тот, на который меняем

                    // Если следующий элемент файла меньше (больше для сортировки по убыванию), чем предыдущий,
                    // то выводим сообщение в консоль, что файл Х отсортирован неверно.
                    // Следующие данные из файла с ошибкой не обрабатываются
                    if (configuration.getDataType().equals(DataType.INTEGER)) {
                        if (configuration.isSortByAscending() && Integer.parseInt(changeableElement) < Integer.parseInt(tempMinOrMaxElement) ||
                                !configuration.isSortByAscending() && Integer.parseInt(changeableElement) > Integer.parseInt(tempMinOrMaxElement)) {
                            System.out.println("Файл " + inputFileList.get(indexOfMinOrMaxTempElement) +
                                    " отсортирован неверно. Дальнейшие данные из этого файла не будут включены в итоговый файл." +
                                    "\n --help для вызова меню.");

                            configuration.setError(true);

                            // Удаляем этот элемент и файл из обрабатываемого списка.
                            changeableListOfElement.remove(indexOfMinOrMaxTempElement);
                            inputFileList.remove(indexOfMinOrMaxTempElement);
                        }
                    }// Если файл типа стринг делаем такое же сравнение по длинам строк
                    else
                        if (configuration.isSortByAscending() && changeableElement.length() < tempMinOrMaxElement.length() ||
                                !configuration.isSortByAscending() && changeableElement.length() > tempMinOrMaxElement.length()){
                            System.out.println("Файл " + inputFileList.get(indexOfMinOrMaxTempElement) +
                                    " отсортирован неверно. Дальнейшие данные из этого файла не будут включены в итоговый файл." +
                                    "\n --help для вызова меню.");

                            configuration.setError(true);

                            // Удаляем этот элемент и файл из обрабатываемого списка.
                            changeableListOfElement.remove(indexOfMinOrMaxTempElement);
                            inputFileList.remove(indexOfMinOrMaxTempElement);
                        }
                } else {
                    inputFileList.remove(indexOfMinOrMaxTempElement);
                }
            }
            writer.close();
        } catch (Exception e) {
            configuration.setError(true);
            e.printStackTrace();
        }
    }
}
