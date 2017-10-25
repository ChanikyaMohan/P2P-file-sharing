package input_output_stream;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import message.Message;

public class IOStreamWriter extends ObjectOutputStream implements ObjectOutput{


	public IOStreamWriter(OutputStream in) throws IOException, SecurityException {
		super(in);
		// TODO Auto-generated constructor stub
	}
	
	
	public void writeInstanceOf (Object object) throws IOException {
    	if(object instanceof Message)
    		((Message) object).write(this);
    	else
    		throw new IOException("Message type not supported");
    }
}
