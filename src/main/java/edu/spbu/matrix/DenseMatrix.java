package edu.spbu.matrix;

import java.io.*;

/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix {

  protected int matrix[][];
  protected int width;
  protected int height;


  public DenseMatrix() {
    this.matrix = null;
    this.width = 0;
    this.height = 0;
  }

  public DenseMatrix(int height, int width){
    this.matrix = new int[height][width];
    this.width = width;
    this.height = height;
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
              int[][] newMatrix = new int[height][width];
              for (int indexH = 0; indexH < chHeight; indexH++){
                for (int indexW = 0; indexW < chWidth; indexW++){
                  newMatrix[indexH][indexW] = matrix[indexH][indexW];
                }
              }
              matrix = newMatrix;
              chWidth = width;
              chHeight = height;
            }

            matrix[i][j] = Integer.valueOf(elem);
          }catch (NumberFormatException e) {
            System.out.println(e.getMessage());
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

          if (j != 0){
            i += 1;
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

  /*
   * Функция для много поточного перемножения
   */
  public void mulMatrixThread(DenseMatrix obj, DenseMatrix result, int firstRow, int lastRow){
    for (int i = firstRow; i < lastRow; i++){
      for (int j = 0; j < obj.height; j++) {
        int sum = 0;
        for (int s = 0; s < obj.width; s++){
          sum += this.matrix[i][s]*obj.matrix[j][s];
        }
        result.matrix[i][j] = sum;
      }
    }
  }

  protected DenseMatrix trans() {
    DenseMatrix tr = new DenseMatrix(this.width, this.height);
    for (int i = 0; i < this.height; i++) {
      for (int j = 0; j < this.width; j++) {
        tr.matrix[j][i] = this.matrix[i][j];
      }
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
  @Override
  public Matrix mul(Matrix o) {

    if (o.getClass() == this.getClass()){
      DenseMatrix obj = (DenseMatrix)o;

      if (obj.width == this.height){
        obj = obj.trans(); //Ссылка на прошлый obj пропадает

        DenseMatrix res = new DenseMatrix(this.height, obj.height);
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
      SparseMatrix mn2 = new SparseMatrix();
      if (o.getClass() == mn2.getClass()) {
        DenseMatrix mn1 = this;
        mn2 = (SparseMatrix)o;
        mn2 = mn2.trans();


        if (mn1.width == mn2.width) {
          DenseMatrix res = new DenseMatrix(mn1.height, mn2.height);

          int k = 0;
          int k1 = 0;
          int i1 = 0;

          for (int i = 0; i < res.height; i++){
            for (int j = 0; j < res.width; j++) {
              res.matrix[i][j] = 0;
            }
          }

          while (mn2.points[i1] != mn2.amount) {
            while ((mn2.points[i1] == mn2.points[i1 + 1]) && (mn2.points[i1] != mn2.amount)){
              i1 += 1;
            }
            if (mn2.points[i1] == mn2.amount) break;
            for (int i = 0; i < mn1.height; i++){
              int sum = 0;
              for (k1 = mn2.points[i1]; k1 < mn2.points[i1 + 1]; k1++) {
                sum += mn2.matrix[k1] * mn1.matrix[i][mn2.colons[k1]];
              }

              if (sum != 0) {
                res.matrix[i][i1] = sum;
              }
            }
            i1 += 1;
          }

          return res;
        } else {
          return  null;
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
  @Override
  public Matrix dmul(Matrix o) {
    final int COUNT_THREADS = 8;

    if (o.getClass() == this.getClass()){
      DenseMatrix mn1 = this;
      DenseMatrix mn2 = (DenseMatrix)o;
      mn2 = mn2.trans();
      if (mn1.width == mn2.width) {
        DenseMatrix res = new DenseMatrix(mn1.height, mn2.height);
        int firstRow = 0;
        DDMulMatrixThread mulThreads[] = new DDMulMatrixThread[COUNT_THREADS];
        for (int index = 0; index < COUNT_THREADS; index++){
          int lastRow = firstRow + mn1.height / COUNT_THREADS;
          if (index < mn1.height % COUNT_THREADS) {++lastRow;}
          mulThreads[index] = new DDMulMatrixThread(mn1, mn2, res, firstRow, lastRow);
          mulThreads[index].start();
          firstRow = lastRow;
        }

        try {
          for (final DDMulMatrixThread mulThread : mulThreads)
            mulThread.join();
        }
        catch (InterruptedException e) {
          e.printStackTrace();
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
   * спавнивает с обоими вариантами
   * @param o
   * @return
   */
  @Override
  public boolean equals(Object o) {

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
