package edu.spbu.matrix;

import java.io.*;

/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix {

  private int matrix[][];
  private int width;
  private int height;


  public DenseMatrix() {
    this.matrix = null;
    this.width = 0;
    this.height = 0;
  }

  public DenseMatrix(int matrix[][], int height, int width){
    this.matrix = matrix;
    this.width = height;
    this.height = width;
  }

  /**
   * загружает матрицу из файла
   * @param fileName
   */
  public DenseMatrix(String fileName) {
    try(FileReader reader = new FileReader(fileName)) {

      matrix = new int[1][1];
      int chWidth = 1;
      width = chWidth;
      int chHeight = 1;
      height = chHeight;

      int c = reader.read();
      int i = 0;
      int j = 0;
      String elem = "";
      while (c != -1) {
        while ((c != (int)(' ')) && (c != (int)('\n')) && (c != (int)('\r')) && (c != -1)){
          elem += (char)c;
          c = reader.read();
        }

        if (!(elem.equals(""))) {
          try {
            boolean change = false;
            if (j >= chWidth) {
              width = chWidth * 3 / 2 + 1;
              change = true;
            }
            if (i >= chHeight) {
              height = chHeight * 3 / 2 + 1;
              change = true;
            }
            if (change){
              int[][] newmatrix = new int[height][width];
              for (int indexH = 0; indexH < chHeight; indexH++){
                for (int indexW = 0; indexW < chWidth; indexW++){
                  newmatrix[indexH][indexW] = matrix[indexH][indexW];
                }
              }
              matrix = newmatrix;
              chWidth = width;
              chHeight = height;
            }

            matrix[i][j] = Integer.valueOf(elem);
          }catch (NumberFormatException e) {
            System.err.println(e.getMessage());
          }
          elem = "";
          j += 1;
        }


        if (c == (int)('\n')){
          if (i == 0){
            width = j;
            chWidth = j;
          }

          /*
           * Неккоректный размер строки
           */
          if ((j != width) && (j != 0)){
            System.out.println("Matrix size error in file: " + fileName);
            System.exit(0);
          }
          i += 1;
          j = 0;
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
      reader.close();

      int[][] newmatrix = new int[height][width];
      for (int indexH = 0; indexH < height; indexH++){
        for (int indexW = 0; indexW < width; indexW++){
          newmatrix[indexH][indexW] = matrix[indexH][indexW];
        }
      }
      matrix = newmatrix;

    }catch(IOException e){
      System.out.println(e.getMessage());
    }
  }

  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o
   * @return
   */
  @Override public Matrix mul(Matrix o) {

    if (o.getClass() == this.getClass()){
      DenseMatrix obj = (DenseMatrix)o;

      if (obj.width == this.height){
        obj = obj.trans(); //Ссылка на прошлый obj пропадает

        DenseMatrix res = new DenseMatrix(new int[this.height][obj.height], this.height, obj.height);
        for (int i = 0; i < this.height; i++) {
          for (int j = 0; j < obj.height; j++) {
            int sum = 0;
            for (int k = 0; k < this.width; k++){
              sum += this.matrix[i][k] * obj.matrix[j][k];
            }
            res.matrix[i][j] = sum;
          }
        }

        return res;
      }
      else{
        System.out.println("Неправильные размеры перемножаемых матриц");
        return null;
      }
    }
    else{
      return null;
    }
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o
   * @return
   */
  @Override public Matrix dmul(Matrix o)
  {
    return null;
  }

  public DenseMatrix trans() {
    DenseMatrix tr = new DenseMatrix(new int[this.width][this.height], this.width, this.height);
    for (int i = 0; i < this.height; i++) {
      for (int j = 0; j < this.width; j++) {
        tr.matrix[j][i] = this.matrix[i][j];
      }
    }
    return tr;
  }

  /**
   * спавнивает с обоими вариантами
   * @param o
   * @return
   */
  @Override public boolean equals(Object o) {

    boolean flag = true;

    if (o.getClass() == this.getClass()){
      DenseMatrix obj = (DenseMatrix)o;
      if ((obj.height != this.height) || (obj.width != this.width)) {
        flag = false;
      }
      else{
        for (int i = 0; i < this.height; i++){
          for (int j = 0; j < this.width; j++){
            if (this.matrix[i][j] != obj.matrix[i][j]){
              flag = false;
              break;
            }
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
