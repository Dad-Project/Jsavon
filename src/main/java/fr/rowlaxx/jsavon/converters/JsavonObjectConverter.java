package fr.rowlaxx.jsavon.converters;

import fr.rowlaxx.convertutils.Return;
import fr.rowlaxx.convertutils.SimpleConverter;
import fr.rowlaxx.jsavon.JsavonObject;

@Return(canReturnInnerType = true)
public class JsavonObjectConverter extends SimpleConverter<JsavonObject> {

	//Constructeurs
	public JsavonObjectConverter() {
		super(JsavonObject.class);
	}
	
	//Methodes
	
}
