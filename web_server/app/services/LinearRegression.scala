package services

import org.ejml.data.{DenseMatrix64F, Matrix64F}
import org.ejml.ops.CommonOps
import scala.collection.mutable.ArrayBuffer
import math.pow

class LinearRegression(val x:Array[Double], val y:Array[Double]) {

  var X,Y,theta:DenseMatrix64F = _
  var (n, m) = (0,0)
  var (minX, maxX) = (0.0, 0.0)

  def loss(): Double = {
    val YP = hypothesis(X)
    val sub = new DenseMatrix64F(m, 1)
    CommonOps.sub(YP, Y, sub)
    var loss = 0.0
    for(i <- 0 until  sub.getNumRows) {
      loss += pow(sub.get(i,0), 2)
    }
    loss/m
  }

  //n dim
  def fit(dims:Int = 5): LinearRegression = {
    assert(x.length == y.length)
    m = x.length
    n = dims + 1
    minX = x.min
    maxX = x.max

    X = new DenseMatrix64F(m, n)
    Y = new DenseMatrix64F(m, 1)
    theta = new DenseMatrix64F(n, 1)
    for(i <- 0 until m) {
      for(j <- 0 until n) {
        val xNorm = (x(i) - minX) / (maxX - minX)
        X.set(i,j,pow(xNorm, j))
      }
    }
    for(i <- 0 until m) {
      Y.set(i,0,y(i))
    }

    val XTranspose = X.copy()
    CommonOps.transpose(XTranspose)
    //println(XTranspose)
    val XTransposeX = new DenseMatrix64F(n, n)
    CommonOps.mult(XTranspose, X, XTransposeX)
    //println(XTransposeX)
    val XTransposeXInv = new DenseMatrix64F(n, n)
    CommonOps.pinv(XTransposeX, XTransposeXInv)
    //println(XTransposeXInv)
    val XTransposeXInvXTranspose = new DenseMatrix64F(n, m)
    CommonOps.mult(XTransposeXInv, XTranspose, XTransposeXInvXTranspose)
    //println(XTransposeXInvXTranspose)
    CommonOps.mult(XTransposeXInvXTranspose, Y, theta)
    //println(Y)
    this
  }

  def hypothesis(XNew:DenseMatrix64F): DenseMatrix64F = {
    val (mNew, nNew) = (XNew.getNumRows, XNew.getNumCols)
    assert(nNew == n)
    val YP = new DenseMatrix64F(mNew, 1)
    CommonOps.mult(XNew, theta, YP)
    YP
  }

  def predict(xNew:Array[Double]): Array[Double] = {
    val mNew = xNew.length
    val XNewMat = new DenseMatrix64F(mNew, n)
    for(i <- 0 until mNew) {
      for(j <- 0 until n) {
        val xNewNorm = (xNew(i) - minX) / (maxX - minX)
        XNewMat.set(i,j,pow(xNewNorm, j))
      }
    }
    val YP  = hypothesis(XNewMat)
    val p = new ArrayBuffer[Double](mNew)
    for(i <- 0 until mNew) {
      p.append(YP.get(i,0))
    }
    p.toArray
  }

}