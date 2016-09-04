package com.cer.enums;


public enum EnumErrores {
	
	ERROR_DEFAULT                     					("500", "Algo fue mal realizando la operacion"),
	ERROR_CURRENCY_SERVER              					("500", "Error interno - currency services -> server"),
	ERROR_RATES_SERVER                 					("500", "Error interno - rates services -> server"),
	ERROR_INTERNAL_GENERA              					("500", "Algo fue mal realizando la operacion"),	
	BAD_REQUEST          	          					("400", "bad request"),
	ERROR_PATH_PARAM_NO_VALID	        	            ("400", "Path param inválido."),
	ERROR_CODE_COUNTRY_SAME		        	            ("400", "Error - El codigo de pais origen y codigo de pais destino deben ser diferentes"),	
	OK					        		                ("200", "OK")
    ;    
    
    private final String cerCodeError;    
    private final String descriptionEs;	

	public String getCerCodeError() {
		return cerCodeError;
	}

	public String getDescriptionEs() {
		return descriptionEs;
	}	    
    
    EnumErrores(String cerCodeError, String descriptionEs){
        this.cerCodeError = cerCodeError;
        this.descriptionEs = descriptionEs;
    }
    
    public static EnumErrores getByCerErrorCode(String errorCode) {
        for (EnumErrores ct : EnumErrores.values()){
            if (ct.getCerCodeError()!=null && ct.getCerCodeError().equalsIgnoreCase(errorCode)) {
                return ct;
            }
        }                
        return ERROR_DEFAULT;
    }    

}
