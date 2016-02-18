package predict4s
package sgp
package ref

import org.scalactic.Or
import org.scalactic.Good
import org.scalactic.Bad
import spire.algebra._
import spire.math._
import spire.implicits._
import spire.syntax.primitives._
import predict4s.sgp._
import predict4s.coord._
import predict4s.coord.LaraConversions._
import predict4s.coord.SGPElemsConversions._
import predict4s.coord.CoordinatesConversions._

class SGP4Lara[F : Field : NRoot : Order : Trig](
  sec : BrouwerLaneSecularCorrections[F]
  ) extends SGP4(sec) with LaraFirstOrderCorrections[F] with SimpleKeplerEq {
 
  override def periodicCorrections(secularElemt : SGPSecularCtx[F]) : SGPSPNResult[F] = {
    val elem = secularElemt._1
		val ictx = secularElemt._2    
    for {
    	eaState <- solveKeplerEq(elem.e, elem.M)
    	spnSecular <- sgpelems2SpecialPolarNodal(eaState, secularElemt)
    	_N = spnSecular.`Θ/r`*spnSecular.r*ictx.c    // N = Θ*cosI , which remains constant
      lnSingular = specialPolarNodal2LaraNonSingular(spnSecular, ictx)
      lalppcorr = lppCorrections(lnSingular, secularElemt)   
      sppt = sppCorrections(lalppcorr)
      finalSPN = laraNonSingular2SpecialPolarNodal(sppt, _N)
    } yield finalSPN
  }

  def periodicCorrectionsLara(secularElemt : SGPSecularCtx[F]) : SGPLaraResult[F] = {
    val elem = secularElemt._1
		val ictx = secularElemt._2    
    for {
    	eaState <- solveKeplerEq(elem.e, elem.M)
    	spnSecular <- sgpelems2SpecialPolarNodal(eaState, secularElemt)
    	_N = spnSecular.`Θ/r`*spnSecular.r*ictx.c    // N = Θ*cosI , which remains constant
      lnSingular = specialPolarNodal2LaraNonSingular(spnSecular, ictx)
      lalppcorr = lppCorrections(lnSingular, secularElemt)   
      sppt = sppCorrections(lalppcorr)
    } yield (sppt, lalppcorr, _N)
  }
   
  def scalePV(uPV: CartesianElems[F], laraCtx: SGPLaraCtx[F]): CartesianElems[F] = {
      import sec.elem0Ctx.wgs.{aE,vkmpersec}, uPV._, laraCtx.{_1=>lns}, lns.{R,r,`Θ/r`}
      val (p, v) = ( (aE*r) *: pos,  vkmpersec *: (R *: pos + `Θ/r` *: vel))
      CartesianElems(p(0),p(1),p(2),v(0),v(1),v(2))
  }    

  def propagateToSPNLPP(secularElemt : SGPSecularCtx[F]) : LPPSPNResult[F] = {
    val elem = secularElemt._1
		val ictx = secularElemt._2    
    val wgs = secularElemt._3
    for {
      eaState <- solveKeplerEq(elem.e, elem.M)
      spnSecular <- sgpelems2SpecialPolarNodal(eaState, secularElemt)
      _N = spnSecular.`Θ/r`*spnSecular.r*ictx.c    // cosI = N/Θ =>  
      lnSingular = specialPolarNodal2LaraNonSingular(spnSecular, ictx)    
      lalppcorr = lppCorrections(lnSingular, secularElemt)
      lppcorr1 = laraNonSingular2SpecialPolarNodal(lalppcorr._1, _N)
    } yield (lppcorr1, LongPeriodContext(0.as[F],0.as[F],0.as[F],0.as[F],0.as[F],0.as[F]), secularElemt)    
  }

  def propagateToCPNLPP(secularElemt : SGPSecularCtx[F]) : LPPCPNResult[F] = {
    val elem = secularElemt._1
		val ictx = secularElemt._2    
    val wgs = secularElemt._3
    for {
      eaState <- solveKeplerEq(elem.e, elem.M)
      spnSecular <- sgpelems2SpecialPolarNodal(eaState, secularElemt)
      _N = spnSecular.`Θ/r`*spnSecular.r*ictx.c    // cosI = N/Θ, N remains a constant 
      lnSingular = specialPolarNodal2LaraNonSingular(spnSecular, ictx)    
      lalppcorr = lppCorrections(lnSingular, secularElemt)
      lppcorr1 = laraNonSingular2CSpecialPolarNodal(lalppcorr._1, _N)
    } yield (lppcorr1, LongPeriodContext(0.as[F],0.as[F],0.as[F],0.as[F],0.as[F],0.as[F]), secularElemt)    
  }

}


object SGP4Lara {
  
  def apply[F : Field : NRoot : Order : Trig](sec: BrouwerLaneSecularCorrections[F]) :  SGP4Lara[F] = new SGP4Lara(sec)
  
  def build[F : Field : NRoot : Order : Trig](tle: TLE, wgs: SGPConstants[F]) :  SGP4Lara[F] Or ErrorMessage = for {
    elem0AndCtx <- SGPElemsConversions.sgpElemsAndContext(tle, wgs)
    secular = BrouwerLaneSecularCorrections(elem0AndCtx)
  } yield SGP4Lara[F](secular)
}