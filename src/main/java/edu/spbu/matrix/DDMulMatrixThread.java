package edu.spbu.matrix;

public class DDMulMatrixThread extends Thread{
  private DenseMatrix result;
  private DenseMatrix firstMatrix;
  private DenseMatrix secondMatrix;
  private int firstPosition;
  private int lastPosition;

  public DDMulMatrixThread(DenseMatrix firstMatrix, DenseMatrix secondMatrix,
                                  DenseMatrix result, int firstPosition, int lastPosition){
    this.firstMatrix = firstMatrix;
    this.secondMatrix = secondMatrix;
    this.result = result;
    this.firstPosition = firstPosition;
    this.lastPosition = lastPosition;
  }

  @Override
  public void run (){
    firstMatrix.mulMatrixThread(secondMatrix, result, firstPosition, lastPosition);
  }
}
