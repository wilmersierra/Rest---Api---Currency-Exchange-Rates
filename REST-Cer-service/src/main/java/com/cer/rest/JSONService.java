package com.cer.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Iterator;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.cer.CurrencyCodeResult;
import com.cer.ForeingExchangeResult;
import com.cer.RateResult;
import com.cer.enums.EnumErrores;

@Path("/json/foreignexchange")
public class JSONService {
	
	final static Logger logger = Logger.getLogger(JSONService.class);
	
	private static final String RESTCOUNTRIES_REST_API_URL = "https://restcountries.eu/rest/v1/alpha/";
	private static final String FIXER_REST_API_URL = "http://api.fixer.io/";
	
	private static final String HTTP_METHOD_GET = "GET";
	private static final String HTTP_ACCEPT = "Accept";
	private static final String HTTP_APPLICATION_JSON = "application/json";
	
	private static final String COUNTRY_SOURCE_PARAM = "countrySource";
	private static final String COUNTRY_TARGET_PARAM = "countryTarget";
	private static final String CURRENCIES_PARAM = "currencies";
	private static final String RATES_PARAM = "rates";
	
	/**
	 * API REST CLIENTE - a partir de los codigos de paises obtener la tasa de cambio de monedas (divias)
	 * Ejemplo Peticion Get: http://localhost:8090/cer-services/rest/json/foreignexchange/countries?countrySource=esp&countryTarget=ita
	 * 04/08/2016
	 * @param countrySource
	 * @param countryTarget
	 * @return
	 */
	@GET
	@Path("/countries")
	@Produces(MediaType.APPLICATION_JSON)
	public ForeingExchangeResult getForeignExchangeInJSON(@QueryParam(COUNTRY_SOURCE_PARAM) String countrySource,
			@QueryParam(COUNTRY_TARGET_PARAM)String countryTarget) {		
		logger.debug("IN getForeignExchangeInJSON");
		ForeingExchangeResult result = new ForeingExchangeResult();
		result.setStatus_code(EnumErrores.ERROR_DEFAULT.getCerCodeError());
		result.setStatus_text(EnumErrores.ERROR_DEFAULT.getDescriptionEs());
		
		//validation
		if(StringUtils.isNotEmpty(countrySource) && StringUtils.isNotEmpty(countrySource)){
			if(!countrySource.toUpperCase().equals(countryTarget.toUpperCase())){				
				//get currency source
				CurrencyCodeResult resultCurrency1 = restClientGetCurrency(RESTCOUNTRIES_REST_API_URL.concat(countrySource));
				result.setStatus_code(resultCurrency1.getStatus_code());
				result.setStatus_text(resultCurrency1.getStatus_text());								
				
				if(result.getStatus_code().equals(EnumErrores.OK.getCerCodeError())){
					result.setCurrencySource(resultCurrency1.getCurrencyCode());					
					//get currency target
					CurrencyCodeResult resultCurrency2 = restClientGetCurrency(RESTCOUNTRIES_REST_API_URL.concat(countryTarget));
					result.setStatus_code(resultCurrency2.getStatus_code());
					result.setStatus_text(resultCurrency2.getStatus_text());
					
					if(result.getStatus_code().equals(EnumErrores.OK.getCerCodeError())){
						result.setCurrencyTarget(resultCurrency2.getCurrencyCode());
						//get rates
						RateResult rateResult = restClientGetRateCurrencies(FIXER_REST_API_URL+"latest?base="+result.getCurrencySource()+"&symbols="+result.getCurrencyTarget());
						if(rateResult.getStatus_code().equals(EnumErrores.OK.getCerCodeError())){
							result.setRate(rateResult.getRate());							
						}else{
							result = resetErrorResult(result);
						}						
					}else{
						result = resetErrorResult(result);
					}					
				}else{
					result = resetErrorResult(result);					
				}
			}else{
				result.setStatus_code(EnumErrores.ERROR_CODE_COUNTRY_SAME.getCerCodeError());
				result.setStatus_text(EnumErrores.ERROR_CODE_COUNTRY_SAME.getDescriptionEs());
			}			
		}else{			
			result.setStatus_code(EnumErrores.BAD_REQUEST.getCerCodeError());
			result.setStatus_text(EnumErrores.BAD_REQUEST.getDescriptionEs());
		}
		logger.debug("IN getForeignExchangeInJSON");
		return result;
	}
	
	private ForeingExchangeResult resetErrorResult(ForeingExchangeResult result){
		result.setCurrencySource(null);
		result.setCurrencyTarget(null);
		result.setRate(null);
		return result;	
	}
	
	/**
	 * 
	 * @author wilmer
	 * @param urlQueryCountry
	 * @return
	 */
	public CurrencyCodeResult restClientGetCurrency(String urlQueryCountry){
		logger.debug("IN restClientGetCurrency");
		CurrencyCodeResult result = new CurrencyCodeResult();
		result.setStatus_code(EnumErrores.ERROR_CURRENCY_SERVER.getCerCodeError());
		result.setStatus_text(EnumErrores.ERROR_CURRENCY_SERVER.getDescriptionEs());
		 try {			 
				URL url = new URL(urlQueryCountry);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod(HTTP_METHOD_GET);
				conn.setRequestProperty(HTTP_ACCEPT, HTTP_APPLICATION_JSON);
				
				result.setStatus_code(String.valueOf(conn.getResponseCode()));
				
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.error(EnumErrores.ERROR_CURRENCY_SERVER.getDescriptionEs().concat("")+"Failed : HTTP error code : "
							+ conn.getResponseCode());
					result.setStatus_text(EnumErrores.ERROR_CURRENCY_SERVER.getDescriptionEs().concat("")+"Failed : HTTP error code : "
							+ conn.getResponseCode());					
				}
				
				if(result.getStatus_code().equals(EnumErrores.OK.getCerCodeError())){					
					BufferedReader br = new BufferedReader(new InputStreamReader(
							(conn.getInputStream())));				 
						String output;
						String resultJson = null;
						while ((output = br.readLine()) != null) {
							resultJson = output;
						}					
						JSONParser parser = new JSONParser();
						if(StringUtils.isNotEmpty(resultJson)){
							try {
								Object obj = parser.parse(resultJson);
								JSONObject jsonObject = (JSONObject) obj;
								JSONArray list = (JSONArray) jsonObject.get(CURRENCIES_PARAM);
								result.setCurrencyCode(String.valueOf(list.get(0)));
								result.setStatus_text(EnumErrores.OK.getDescriptionEs());
							} catch (ParseException e) {			
								e.printStackTrace();
							}			
						}
					
				}		 
				conn.disconnect();		 
			  } catch (MalformedURLException e) {
		 
				e.printStackTrace();
		 
			  } catch (IOException e) {
		 
				e.printStackTrace();
		 
			  }
		logger.debug("OUT restClientGetCurrency");
		return result;
		
	}
	
	/**
	 * 
	 * @param urlQueryCountry
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public RateResult restClientGetRateCurrencies(String urlQueryCountry){
		logger.debug("OUT restClientGetRateCurrencies");
		RateResult result = new RateResult();
		result.setStatus_code(EnumErrores.ERROR_RATES_SERVER.getCerCodeError());
		result.setStatus_text(EnumErrores.ERROR_RATES_SERVER.getDescriptionEs());
		 try {
			 
				URL url = new URL(urlQueryCountry);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod(HTTP_METHOD_GET);
				conn.setRequestProperty(HTTP_ACCEPT, HTTP_APPLICATION_JSON);
				
				result.setStatus_code(String.valueOf(conn.getResponseCode()));
				
				if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
					logger.error(EnumErrores.ERROR_RATES_SERVER.getDescriptionEs().concat("")+"Failed : HTTP error code : "
							+ conn.getResponseCode());
					result.setStatus_text(EnumErrores.ERROR_RATES_SERVER.getDescriptionEs().concat("")+"Failed : HTTP error code : "
							+ conn.getResponseCode());					
				}
				
				if(result.getStatus_code().equals(EnumErrores.OK.getCerCodeError())){
					
					BufferedReader br = new BufferedReader(new InputStreamReader(
							(conn.getInputStream())));
				 
						String output;
						String resultJson = null;
						while ((output = br.readLine()) != null) {
							resultJson = output;
						}					
						JSONParser parser = new JSONParser();
						if(StringUtils.isNotEmpty(resultJson)){
							try {
								Object obj = parser.parse(resultJson);
								JSONObject jsonObject = (JSONObject) obj;
								JSONObject jsonObjectRates = (JSONObject) jsonObject.get(RATES_PARAM);
								result.setRate("");
								Collection list =jsonObjectRates.values();
								for(Iterator i=list.iterator(); i.hasNext();){
									Object obj1 = i.next();
									result.setRate(String.valueOf(obj1));
									
								}
								result.setStatus_text(EnumErrores.OK.getDescriptionEs());
							} catch (ParseException e) {			
								e.printStackTrace();
							}			
						}
					
				}			
		 
				conn.disconnect();
		 
			  } catch (MalformedURLException e) {
		 
				e.printStackTrace();
		 
			  } catch (IOException e) {
		 
				e.printStackTrace();
		 
			  }
		logger.debug("OUT restClientGetRateCurrencies");
		return result;
		
	}
	
}
