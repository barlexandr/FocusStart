package ru;

import ru.configuration.Configuration;
import ru.configuration.DataType;
import ru.sort.MergeSort;

public class Main {
    public static void main(String[] args) throws Exception {
        // Сначала рассматриваем пришедшие аргументы. Выделяем параметры сортировки, входные и выходной файл.
        // В зависимости от входных данных (инт, стринг) выполняем сортировку
        Configuration configuration = new Configuration(args);

        MergeSort mergeSort = new MergeSort(configuration);
        mergeSort.sort();

        if (!configuration.isError())
            System.out.println("Файл успешно отсортирован. \n --help для вызова меню.");
    }
}
