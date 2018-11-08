package edu.spbu.matrix;

public class SSMulMatrixThread extends Thread{
  private SparseMatrix result;
  private SparseMatrix firstMatrix;
  private SparseMatrix secondMatrix;
  private int row;

  public SSMulMatrixThread(SparseMatrix firstMatrix, SparseMatrix secondMatrix,
                           SparseMatrix result, int row){
    this.firstMatrix = firstMatrix;
    this.secondMatrix = secondMatrix;
    this.result = result;
    this.row = row;
  }

  @Override
  public void run (){
    firstMatrix.mulMatrixThread(secondMatrix, result, row);
  }
}
