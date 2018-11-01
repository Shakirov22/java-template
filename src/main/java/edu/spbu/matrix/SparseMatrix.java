package edu.spbu.matrix;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Разряженная матрица
 */
public class SparseMatrix implements Matrix
{

  protected int matrix[]; //Множество всех ненулевых элементов
  protected int colons[]; //В каком столбце находится соответсвующий элемент
  protected int points[]; //Позиция в colons, с которой начинается следующая строка
  protected int width;
  protected int height;
  protected int amount;   //Колличество ненулевых элементов

  public SparseMatrix(){
    this.matrix = null;
    this.colons = null;
    this.points = null;
    this.width = 0;
    this.height = 0;
    this.amount = 0;
  }

  public SparseMatrix(int height, int width, int amount){
    this.matrix = new int[amount];
    this.colons = new int[amount];
    this.points = new int[height + 1];
    this.points[0] = 0;
    this.width = width;
    this.height = height;
    this.amount = amount;
  }

  /**
   * загружает матрицу из файла
   * @param fileName
   */
  public SparseMatrix(String fileName) {
    this(1, 1, 1);
    try(FileReader reader = new FileReader(fileName)) {

      int chAmount = 1;
      int chHeight = 1;

      int c = reader.read();
      int i = 0;
      int j = 0;
      int k = 0;
      String elem = "";
      while (c != -1) {
        while ((c != (int)(' ')) && (c != (int)('\n')) && (c != (int)('\r')) && (c != -1)){
          elem += (char)c;
          c = reader.read();
        }

        if (!(elem.equals(""))) {
          try {
            int element = Integer.valueOf(elem);
            if (element != 0){

              if (k >= chAmount) {
                amount = chAmount * 3 / 2 + 1;

                int[] newMatrix = new int[amount];
                int[] newColons = new int[amount];
                for (int indexA = 0; indexA < chAmount; indexA++){
                  newMatrix[indexA] = matrix[indexA];
                  newColons[indexA] = colons[indexA];
                }
                matrix = newMatrix;
                colons = newColons;
                chAmount = amount;
              }

              matrix[k] = element;
              colons[k] = j;
              k += 1;
            }

            elem = "";
            j += 1;
          }catch (NumberFormatException e) {
            System.out.println(e.getMessage());
          }
        }


        if (c == (int)('\n')){
          if (i == 0){
            width = j;
          }

          /*
           * Неккоректный размер строки
           */
          if ((j != width) && (j != 0)){
            System.out.println("Matrix size error in file: " + fileName);
            System.exit(0);
          }

          if (j != 0){
            i += 1;

            if (i >= chHeight) {
              height = chHeight * 3 / 2 + 1;

              int[] newPoints = new int[height];
              for (int indexH = 0; indexH < chHeight; indexH++){
                newPoints[indexH] = points[indexH];
              }
              points = newPoints;
              chHeight = height;
            }
            points[i] = k;

            j = 0;
          }
        }


        if (c != -1){
          c = reader.read();
        }
      }



      if (j == 0) {
        height = i;
      } else {
        height = i + 1;
      }
      amount = k;
      reader.close();


      int[] newPoints = new int[height + 1];
      for (int indexH = 0; indexH < height; indexH++){
        newPoints[indexH] = points[indexH];
      }
      newPoints[height] = amount;
      points = newPoints;

      int[] newMatrix = new int[amount];
      int[] newColons = new int[amount];
      for (int indexA = 0; indexA < amount; indexA++){
        newMatrix[indexA] = matrix[indexA];
        newColons[indexA] = colons[indexA];
      }
      matrix = newMatrix;
      colons = newColons;

    }catch(IOException e){
      System.out.println(e.getMessage());
    }
  }

  protected SparseMatrix trans() {
    SparseMatrix tr = new SparseMatrix(this.width, this.height, this.amount);

    ArrayList<ArrayList> chMatrix =  new ArrayList<> (this.width);
    ArrayList<ArrayList> chColons =  new ArrayList<> (this.width);
    for (int l = 0; l < this.width; l++){
      chMatrix.add (null);
      chColons.add (null);
    }

    int i = 0;
    for (int k = 0; k < this.amount; k++){
      while (k == this.points[i + 1]) {
        i += 1;
      }
      int j = this.colons[k];

      ArrayList<Integer> xMatrix = chMatrix.get(j);
      ArrayList<Integer> xColons = chColons.get(j);
      if (xMatrix == null) {
        xMatrix = new ArrayList();
        xColons = new ArrayList();
      }
      xMatrix.add(matrix[k]);
      chMatrix.set(j, xMatrix);
      xColons.add(i);
      chColons.set(j, xColons);
    }

    int k = 0;
    for (i = 0; i < this.width; i++) {
      ArrayList<Integer> xMatrix = chMatrix.get(i);
      ArrayList<Integer> xColons = chColons.get(i);
      if (xMatrix != null) {
        for(int j = 0; j < xMatrix.size(); j++){
          tr.matrix[k] = (int) xMatrix.get(j);
          tr.colons[k] = (int) xColons.get(j);
          k++;
        }
      }
      tr.points[i + 1] = k;
    }

    return tr;
  }

  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o
   * @return
   */
  @Override public Matrix mul(Matrix o) {

    if (o.getClass() == this.getClass()) {
      SparseMatrix mn1 = this;
      SparseMatrix mn2 = (SparseMatrix) o;
      mn2 = mn2.trans();

      if (mn1.width == this.height){
        SparseMatrix res = new SparseMatrix(mn1.height, mn2.width,  mn1.amount);

        int k = 0;
        int k1 = 0;
        int k2 = 0;
        int i1 = 0;
        int i2 = 0;
        int i2B = 0;
        boolean f2 = true;

        while (mn1.points[i1] != mn1.amount) {
          while ((mn1.points[i1] == mn1.points[i1 + 1]) && (mn1.points[i1] != mn1.amount)){
            i1 += 1;
            res.points[i1] = k;
          }
          if (mn1.points[i1] == mn1.amount) break;

          i2 = i2B;
          while (mn2.points[i2] != mn2.amount){
            while ((mn2.points[i2] == mn2.points[i2 + 1]) && (mn2.points[i2] != mn2.amount)){
              i2 += 1;
            }
            if (mn2.points[i2] == mn2.amount) break;

            if ((mn2.points[i2] == 0) && f2) {
              i2B = i2;
              f2 = false;
            }

            int sum = 0;
            k1 = mn1.points[i1];
            k2 = mn2.points[i2];
            while ((k1 < mn1.points[i1 + 1]) && (k2 < mn2.points[i2 + 1])){
              if (mn1.colons[k1] == mn2.colons[k2]) {
                sum += mn1.matrix[k1] * mn2.matrix[k2];
                k1 += 1;
                k2 += 1;
              } else {
                if (mn1.colons[k1] > mn2.colons[k2]) {
                  k2 += 1;
                }else {
                  k1 += 1;
                }
              }
            }

            if (sum != 0) {
              if (k >= res.amount) {
                res.amount = res.amount * 3 / 2 + 1;

                int[] newMatrix = new int[res.amount];
                int[] newColons = new int[res.amount];
                for (int index = 0; index < k; index++){
                  newMatrix[index] = res.matrix[index];
                  newColons[index] = res.colons[index];
                }
                res.matrix = newMatrix;
                res.colons = newColons;
              }

              res.matrix[k] = sum;
              res.colons[k] = i2;
              k += 1;
            }
            i2 += 1;
          }
          i1 += 1;
          res.points[i1] = k;
        }

        for (; i1 <= res.height; i1++) {
          res.points[i1] = k;
        }

        res.amount = k;
        int[] newMatrix = new int[k];
        int[] newColons = new int[k];
        for (int index = 0; index < k; index++){
          newMatrix[index] = res.matrix[index];
          newColons[index] = res.colons[index];
        }
        res.matrix = newMatrix;
        res.colons = newColons;


        return res;

      } else {
        return null;
      }
    } else {
      DenseMatrix mn2 = new DenseMatrix();
      if (o.getClass() == mn2.getClass()){
        SparseMatrix mn1 = this;
        mn2 = (DenseMatrix)o;
        mn2 = mn2.trans();

        if (mn1.width == mn2.width) {
          SparseMatrix res = new SparseMatrix(mn1.height, mn2.width, mn1.amount);
          int k = 0;
          int k1 = 0;
          int i1 = 0;

          while (mn1.points[i1] != mn1.amount) {
            while ((mn1.points[i1] == mn1.points[i1 + 1]) && (mn1.points[i1] != mn1.amount)){
              i1 += 1;
              res.points[i1] = k;
            }
            if (mn1.points[i1] == mn1.amount) break;
            for (int i = 0; i < mn2.height; i++){
              int sum = 0;
              for (k1 = mn1.points[i1]; k1 < mn1.points[i1 + 1]; k1++) {
                sum += mn1.matrix[k1] * mn2.matrix[i][mn1.colons[k1]];
              }

              if (sum != 0) {
                if (k >= res.amount) {
                  res.amount = res.amount * 3 / 2 + 1;
                  int[] newMatrix = new int[res.amount];
                  int[] newColons = new int[res.amount];
                  for (int index = 0; index < k; index++) {
                    newMatrix[index] = res.matrix[index];
                    newColons[index] = res.colons[index];
                  }
                  res.matrix = newMatrix;
                  res.colons = newColons;
                }
                res.matrix[k] = sum;
                res.colons[k] = i;
                k += 1;
              }
            }
            i1 += 1;
            res.points[i1] = k;
          }

          for (; i1 <= res.height; i1++) {
            res.points[i1] = k;
          }

          res.amount = k;
          int[] newMatrix = new int[k];
          int[] newColons = new int[k];
          for (int index = 0; index < k; index++){
            newMatrix[index] = res.matrix[index];
            newColons[index] = res.colons[index];
          }
          res.matrix = newMatrix;
          res.colons = newColons;


          return res;
        } else{
          return null;
        }

      } else {
        return null;
      }
    }
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o
   * @return
   */
  @Override public Matrix dmul(Matrix o) {
    return null;
  }

  /**
   * спавнивает с обоими вариантами
   * @param o
   * @return
   */
  @Override public boolean equals(Object o) {
    boolean flag = true;

    if (o.getClass() == this.getClass()){
      SparseMatrix obj = (SparseMatrix) o;
      if ((obj.height != this.height) || (obj.width != this.width) || (obj.amount != this.amount)) {
        flag = false;
      }
      else{
        for (int i = 0; i < this.amount; i++){
          if ((this.matrix[i] != obj.matrix[i]) || (this.colons[i] != obj.colons[i])){
            flag = false;
            break;
          }
        }
        for (int i = 0; i < this.height; i++){
          if (this.points[i] != obj.points[i]){
            flag = false;
            break;
          }
        }
      }
    }
    else{
      flag = false;
    }

    return flag;
  }
}
