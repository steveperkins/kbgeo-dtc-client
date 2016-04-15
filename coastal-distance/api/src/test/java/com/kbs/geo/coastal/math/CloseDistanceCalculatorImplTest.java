package com.kbs.geo.coastal.math;

import static org.junit.Assert.*;

import java.math.BigDecimal;

import org.junit.Test;

import com.kbs.geo.coastal.model.CoastlinePoint;
import com.kbs.geo.coastal.model.LatLng;

public class CloseDistanceCalculatorImplTest {

	@Test
	public void testCalculate() {
		LatLng targetPoint = new LatLng(32.52112935952776,-80.26861846446991);
		CoastlinePoint coastlinePoint1 = new CoastlinePoint(32.52116554544883,-80.26817321777344, null);
		CoastlinePoint coastlinePoint2 = new CoastlinePoint(32.520880580925926,-80.26883840560913, null);
		CoastlinePoint coastlinePoint3 = new CoastlinePoint(32.52065894122782,-80.26936411857605, null);
		
		CloseDistanceCalculatorImpl calculator = new CloseDistanceCalculatorImpl(targetPoint, coastlinePoint1, coastlinePoint2, coastlinePoint3);
		DistanceCalculatorResult result = calculator.calculate();
		// The result should be null because there is no perpendicular intersection between the target point and either of the two sets of coastline points
		assertNotNull(result);
		System.out.println(String.format("New point: %f,%f at %f miles from %f,%f", result.getCoastlinePoint().getLat(), result.getCoastlinePoint().getLng(), result.getDistanceInMiles(), targetPoint.getLat(), targetPoint.getLng()));
		assertEquals(new Double(0.009596227052930206), result.getDistanceInMiles());
		assertEquals(new Double(32.52099876948125), result.getCoastlinePoint().getLat().doubleValue(), 0.001);
		assertEquals(new Double(-80.268562520076), result.getCoastlinePoint().getLng().doubleValue(), 0.001);
	}
	
	@Test
	public void testCalculateActualCoastalPointIsClosest() {
		LatLng targetPoint = new LatLng(44.585271,-68.231347);
		CoastlinePoint coastlinePoint1 = new CoastlinePoint(44.5224440,-68.2113890, null);
		CoastlinePoint coastlinePoint2 = new CoastlinePoint(44.5256670,-68.2160000, null);
		CoastlinePoint coastlinePoint3 = new CoastlinePoint(44.5214720,-68.2194170, null);
		
		CloseDistanceCalculatorImpl calculator = new CloseDistanceCalculatorImpl(targetPoint, coastlinePoint1, coastlinePoint2, coastlinePoint3);
		DistanceCalculatorResult result = calculator.calculate();
		assertNull(result);
	}
	
	@Test
	public void testCalculateKennebunkME() {
		LatLng targetPoint = new LatLng(43.3838889,-70.5452778);
		CoastlinePoint coastlinePoint1 = new CoastlinePoint(43.336639,-70.535028, null);
		CoastlinePoint coastlinePoint2 = new CoastlinePoint(43.344444,-70.516, null);
		CoastlinePoint coastlinePoint3 = new CoastlinePoint(43.340833,-70.500861, null);
		
		CloseDistanceCalculatorImpl calculator = new CloseDistanceCalculatorImpl(targetPoint, coastlinePoint1, coastlinePoint2, coastlinePoint3);
		DistanceCalculatorResult result = calculator.calculate();
		assertNotNull(result);
		assertEquals(new Double(3.1764644855771262), result.getDistanceInMiles());
		assertTrue(new BigDecimal(43.33984512676995).subtract(result.getCoastlinePoint().getLat()).abs().doubleValue() <= 0.001);
		assertTrue(new BigDecimal(-70.52721170529422113).subtract(result.getCoastlinePoint().getLng()).abs().doubleValue() <= 0.001);
	}
	
	
	@Test
	public void testCalculateHauloverBeachFL() {
		LatLng targetPoint = new LatLng(25.8915039288681, -80.16963958740234);
		CoastlinePoint coastlinePoint1 = new CoastlinePoint(25.903722,-80.143306, null);
		CoastlinePoint coastlinePoint2 = new CoastlinePoint(25.895806,-80.151278, null);
		CoastlinePoint coastlinePoint3 = new CoastlinePoint(25.865833,-80.172111, null);
		
		CloseDistanceCalculatorImpl calculator = new CloseDistanceCalculatorImpl(targetPoint, coastlinePoint1, coastlinePoint2, coastlinePoint3);
		DistanceCalculatorResult result = calculator.calculate();
		assertNotNull(result);
		assertEquals(new Double(0.8143498214971795), result.getDistanceInMiles());
		assertTrue(new BigDecimal(25.88430011587564).subtract(result.getCoastlinePoint().getLat()).abs().doubleValue() <= 0.001);
		assertTrue(new BigDecimal(-80.15927526700573).subtract(result.getCoastlinePoint().getLng()).abs().doubleValue() <= 0.001);
	}
	
	@Test
	public void testCalculateHauloverBeachFLJustNorthOfCoastalPoint() {
		LatLng targetPoint = new LatLng(25.87034506238234, -80.17341613769531);
		CoastlinePoint coastlinePoint1 = new CoastlinePoint(25.895806,-80.151278, null);
		CoastlinePoint coastlinePoint2 = new CoastlinePoint(25.865833,-80.172111, null);
		CoastlinePoint coastlinePoint3 = new CoastlinePoint(25.845833,-80.173750, null);
		
		CloseDistanceCalculatorImpl calculator = new CloseDistanceCalculatorImpl(targetPoint, coastlinePoint1, coastlinePoint2, coastlinePoint3);
		DistanceCalculatorResult result = calculator.calculate();
		assertNotNull(result);
		assertEquals(new Double(0.23531487113775698), result.getDistanceInMiles());
		assertTrue(new BigDecimal(25.86826365077612).subtract(result.getCoastlinePoint().getLat()).abs().doubleValue() <= 0.001);
		assertTrue(new BigDecimal(-80.1704215545785).subtract(result.getCoastlinePoint().getLng()).abs().doubleValue() <= 0.001);
	}
	
	@Test
	public void testCalculate80PineStNY() {
		LatLng targetPoint = new LatLng(40.7061656,-74.0090105);
		CoastlinePoint coastlinePoint1 = new CoastlinePoint(40.603678,-74.053125, 94221.0);
		CoastlinePoint coastlinePoint2 = new CoastlinePoint(40.609782,-74.035149, 94220.0);
		CoastlinePoint coastlinePoint3 = new CoastlinePoint(40.604421,-74.032856, 94219.0);
		
		CloseDistanceCalculatorImpl calculator = new CloseDistanceCalculatorImpl(targetPoint, coastlinePoint1, coastlinePoint2, coastlinePoint3);
		DistanceCalculatorResult result = calculator.calculate();
		// The result should be null because there is no perpendicular intersection between the target point and either of the two sets of coastline points
		assertNull(result);
	}
	
	@Test
	public void testCalculateLakeInLA() {
		LatLng targetPoint = new LatLng(30.334118,-90.321844);
		CoastlinePoint coastlinePoint1 = new CoastlinePoint(30.321174, -90.285816, 0.0);
		CoastlinePoint coastlinePoint2 = new CoastlinePoint(30.317321,-90.289249, 0.0);
		CoastlinePoint coastlinePoint3 = new CoastlinePoint(30.317321,-90.289249, 0.0);
		
		CloseDistanceCalculatorImpl calculator = new CloseDistanceCalculatorImpl(targetPoint, coastlinePoint1, coastlinePoint2, coastlinePoint3);
		DistanceCalculatorResult result = calculator.calculate();
		// The result should be null because there is no perpendicular intersection between the target point and either of the two sets of coastline points
		assertNull(result);
	}


}
