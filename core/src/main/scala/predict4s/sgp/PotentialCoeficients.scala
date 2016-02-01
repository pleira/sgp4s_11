package predict4s.sgp

import spire.algebra._
import spire.math._
import spire.implicits._
import spire.syntax.primitives._
import predict4s.coord._

// The notation used in the formulas here correspond to that used in SPACETRACK Report n 3, Hoots.

case class GeoPotentialCoefs[F](C1: F, C2: F, C3: F, C4: F, C5: F, D2: F, D3: F, D4: F)

trait GeoPotentialAndAtmosphere2ndOrderModel {

  def geoPotentialCoefs[F: Field : NRoot : Order : Trig](elem0Ctx: SGPElemsCtx[F], gctx: GeoPotentialContext[F]) 
      : GeoPotentialCoefs[F] = {
    import elem0Ctx.{elem,iCtx,eCtx,wgs,perigeeHeight} 
    import gctx._
    import wgs.{J2,J3,`J3/J2`}
    import elem0.{e=>e0,n=>n0,ω=>ω0,a=>a0,bStar},iCtx.{sinI0,`θ²`},eCtx.`β0²`
  
    val coef1 : F = `ξ⁴(q0-s)⁴` / (`1-η²`** 3.5)
  
    val C2 : F = coef1 * n0 *(a0 * (1 + 1.5*`η²` + e0η*(4 + `η²`)) + 0.375*J2*ξ / `1-η²` * (3*`θ²` - 1) * (8 + 3*`η²`*(8 + `η²`)))
      // coef1 * n0 *(a0 * (1 + 1.5*`η²` + e0η*(4 + `η²`)) + 3*J2*ξ / 2 / psisq * (3*`θ²` - 1) / 2 * (8 + 3*`η²`*(8 + `η²`)))
    
    val C1 : F = bStar * C2
    val `C1²`  = C1*C1
  
    val C3 =  if (e0 > 0.0001.as[F]) -2 * `ξ⁴(q0-s)⁴` * ξ * `J3/J2` * n0 * sinI0 / e0 else 0.as[F]
    val aterm = 3*(1-3*`θ²`)*(1 + 3*`η²`/2 - 2*e0η - e0η*`η²`/2) + 3*(1-`θ²`)*(2*`η²` - e0η - e0η*`η²`)*cos(2*ω0)/4
    val C4 = 2*a0*`β0²`*coef1*n0*((2*η*(1 + e0η) + (e0 + `η³`)/2) - J2*ξ*aterm/(a0*`1-η²`))
    val C5 = 2*a0*`β0²`*coef1*(1 + 11*(`η²`+e0η)/4 + e0η*`η²`)
    val D2 = 4*a0*`C1²`*ξ
    val D3 = D2*(17*a0+s)*C1*ξ/3
    val D4 = D2*D2*ξ*(221*a0+31*s)/24
    GeoPotentialCoefs(C1,C2,C3,C4,C5,D2,D3,D4)
  }
  
}

case class GeoPotentialContext[F: Field: NRoot : Order: Trig](
    elem0 : SGPElems[F],   // original elements with a0 and n0 
    s: F,                  // atmospheric coefficient
    aE: F                  // earths radius  (km)
    ) {
  import elem0.{n => n0,e => e0, a => a0}

  val ξ = 1 / (a0 - s)  // tsi
  val η = a0*e0*ξ   // eta
  val `η²` = η*η
  val `η³` = η*`η²`
  
  val e0η = e0*η      // eeta 
  val `1-η²` = abs[F](1-`η²`) 
  // The parameter q0 is the geocentric reference altitude, 
  // a constant equal to 120 km plus one Earth radius 
  val q0 = 1 + 120/aE 
  val `ξ⁴(q0-s)⁴` = (ξ*(q0 - s))**4 
}
