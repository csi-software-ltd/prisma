import org.codehaus.groovy.grails.commons.ConfigurationHolder
import java.util.regex.Matcher
import java.util.regex.Pattern
import java.security.MessageDigest
import java.security.SecureRandom

class Tools  {     
  ///////////////////////////////////////////////////////////////////////////////
  static prepareEmailString(sEmail){
    // remove -,.,@
    if(sEmail==null)
      return ''
    return sEmail.replace("@", '').replace('-','').replace('.','')
  }
  ///////////////////////////////////////////////////////////////////////////////
  static checkEmailString(sEmail){
    return sEmail ==~ /^[_A-Za-z0-9](([_\.\-]?[a-zA-Z0-9]+)*)[_]*@([A-Za-z0-9]+)(([\.\-]?[a-zA-Z0-9]+)*)\.([A-Za-z]{2,})$/
  }
  /////////////////////////////////////////////////////////////////////////////
  static generateMD5(sText) {
    MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
    digest.update(sText.getBytes());
    def mdRes=digest.digest()
    def sOut=''
    for (i in mdRes) 
      sOut+=Integer.toHexString(0xFF & i)
    return sOut;  
  }
  /////////////////////////////////////////////////////////////////////////////
  static hidePsw(sPsw) {
    return generateMD5('_yellcat'+sPsw+'yellcat-')
	/*def psw='_yellcat'+sPsw+'yellcat-'
	return psw.encodeAsMD5()*/
  }
  /////////////////////////////////////////////////////////////////////////////
  static getIntVal(sValue,iDefault=0){
    if(sValue==null)
      return iDefault
    try{
      iDefault=sValue.toInteger()
    }catch(Exception e){
      //do nothing
    }
    return iDefault
  }
  /////////////////////////////////////////////////////////////////////////////
  static getLongVal(sValue,iDefault=0){
    if(sValue==null)
      return iDefault
    try{
      iDefault=sValue.toLong()
    }catch(Exception e){
      //do nothing
    }
    return iDefault
  }
  /////////////////////////////////////////////////////////////////////////////
  static Float getFloatVal(sValue,fDefault=0f){
    if(sValue==null)
      return fDefault
    try{
      fDefault=sValue.toFloat()
    }catch(Exception e){
      //do nothing
    }
    return fDefault
  }
  static Date getDate(sName){
    if(!sName)
      return null
    try{
      return Date.parse('dd.MM.yyyy', sName)
    }catch(Exception e){
      return null
    }
  }
  static Date getDateShort(sName){
    if(!sName)
      return null
    try{
      return Date.parse('dd.MM.yy', sName)
    }catch(Exception e){
      return null
    }
  }
  ///////////////////////////////////////////////////////////////////////////
  static String arrayToString(sValue,separator) {
    if(((sValue!=null)?sValue:[]).size()==0)
      return ''
    StringBuffer result = new StringBuffer();
    if (sValue.size() > 0) {
      result.append(sValue[0]);
      for (int i=1; i<sValue.size(); i++) {
        result.append(separator);
        result.append(sValue[i]);
      }
    }
    return result.toString();
  }
  ///////////////////////////////////////////////////////////////////////////
  static String escape(sValue){
    return sValue.replace("'","\\'").replace('"','\\"')
  }
  static fixHtml(sText,sFrom){
    if(!(sText?:'').size())
	  return ''
    def start=false
	def lsTags=[]
	switch(sFrom){
	  case 'admin': start=true;
        lsTags=['u','i','em','b','ol','ul','li','s','sub','sup','address','pre','p',
        'h1','h2','h3','h4','h5','h6','strong']
		break		
	  case 'personal':	
        if(Tools.getIntVal(ConfigurationHolder.config.editor.fixHtml)) start=true
        lsTags=['u','i','em','b','ol','ul','li','s','sub','sup','address','pre','p',
        'h1','h2','h3','h4','h5','h6','strong']
		break
	}	
	sText=sText.replace("\r",' ').replace("\n",' ').replace("'",'"')
	if(start){      
      sText=sText.replace("[YELLclose]",'').replace("[YELLspan]",'')
      sText=sText.replace('<br />','[YELLbr]')
      sText=sText.replace('<br>','[YELLbr]')
      sText=sText.replace('</span>','[/YELLspan]')
    
      sText=sText.replaceAll( /(<span )(style="[^\">]*?;")(>)/,'[YELLspan] $2[YELLclose]')
    
      for(sTag in lsTags) //TODO? change pre into p?
        sText=sText.replace('<'+sTag+'>','[YELL'+sTag+']').replace('</'+sTag+'>','[/YELL'+sTag+']')  
    
      sText=sText.replace('<',' &lt; ').replace('>',' &gt; ')
    
      for(sTag in lsTags) //TODO? change pre into p?
        sText=sText.replace('[YELL'+sTag+']','<'+sTag+'>').replace('[/YELL'+sTag+']','</'+sTag+'>')  
    
      sText=sText.replace('[YELLspan]','<span').replace('[YELLclose]','>').replace('[/YELLspan]','</span>')
      sText=sText.replace('[YELLbr]','<br />')      
    }
    return sText
  }  

  static String generateSMScode() {
    Random rand = new Random(System.currentTimeMillis())
    return (rand.nextInt().abs() % 89999 + 10000).toString() //10000..99999
  }
  
  static String generatePassword(){
    Random RANDOM = new SecureRandom();
    def letters = "abcdefghjkmnpqrstuvwxyz"
    def firstLetters="ABCDEFGHJKMNPQRSTUVWXYZ"
    def digits="1234567890";    
    
    def pw = ""
    def digitsPw = ""
    def firstLettersPw = ""  
    def passwordlength=Tools.getIntVal(ConfigurationHolder.config.user.passwordlength,7)        
    
    def firstLettersLength=1+RANDOM.nextInt(2)   
    def digitsLength=1+RANDOM.nextInt(2)    
    def lsFirst=[]
    
    for (int i=0; i<passwordlength; i++){
        int index = (int)(RANDOM.nextDouble()*letters.length());
        pw += letters.substring(index, index+1);
    }
    for (int i=0; i<firstLettersLength; i++){ 
        int index = (int)(RANDOM.nextDouble()*firstLetters.length());
        firstLettersPw += firstLetters.substring(index, index+1);
    }
    for (int i=0; i<digitsLength; i++){
        int index = (int)(RANDOM.nextDouble()*digits.length());
        digitsPw += digits.substring(index, index+1);
    }    
    
    for(int i=0;i<firstLettersLength;i++){
      def number=(int)(RANDOM.nextDouble()*pw.length())
      pw=Tools.replaceCharAt(pw,number,firstLettersPw.charAt(i))      
      lsFirst<<number
    }
    
    for(int i=0;i<digitsLength;i++){
      def number
      
      for(;;){ 
        number=(int)(RANDOM.nextDouble()*pw.length())
        if(!lsFirst.contains(number)) //condition to break, oppossite to while 
          break        
      }           
      pw=Tools.replaceCharAt(pw,number,digitsPw.charAt(i)) 
    }
    
    return pw;
  }
  
  public static String replaceCharAt(String s, int pos, char c) {
    StringBuffer buf = new StringBuffer( s );
    buf.setCharAt( pos, c );
    return buf.toString( );
  }

  static String generateModeParam(lId,lCId) {
    def i = 0
    return generateMD5('_yellcat'+lId+'somesalt'+lCId+'yellcat-').toCharArray().collect{++i; i%4?'':it}.join()
  }
  static String generateModeParam(lId) {
    return generateModeParam(lId,0)
  }

  static String generateSnils(lId,iType) {
    def snils = []
    lId.toString().reverse().eachWithIndex { it, index -> snils << it; index%3!=2?null:(snils << '-') }
    while(snils.size<11){
        snils.size%4==3? snils << '-' : null
        snils << '0'
    }
    snils.join().reverse()+' 0' + iType
  }

  static boolean checkIpRange(sIp){   
    def lsIp=sIp.split('\\.')
    if(lsIp.size()==4){  
      def ip_last=lsIp[3]
      def ipTmp=lsIp[0]+'.'+lsIp[1]+'.'+lsIp[2]      
                      
      def sUips=Dynconfig.findByName('allowed.ips')?.value?:''
      
      for(oUip in sUips.split(',')){      
        def sUip=oUip.split('\\.')
        if(sUip.size()==4){
          def sUip_main_ip_part=sUip[0]+'.'+sUip[1]+'.'+sUip[2]
          def sUip_start=sUip[3].split('-')[0]
          def sUip_end=sUip[3].split('-')[1]
       
          if(sUip_main_ip_part==ipTmp && sUip_start<=ip_last && sUip_end>=ip_last)
            return true
        }  
      }
    }          
    return false    
  }

  static Integer computeMonthDiff(dStartdate, dEnddate){
    if (dEnddate<=dStartdate) return 0
    return ((dEnddate.getYear() - dStartdate.getYear())*12+(dEnddate.getMonth() - dStartdate.getMonth()))+1
  }

  static Integer computeMonthWorkDays(iMonth, iYear){
    if (!iMonth||!iYear) return 0
    def computeDate = new Date(iYear-1900,iMonth-1,1)
    Integer icount = 0
    while(computeDate.getMonth()==iMonth-1){
      if ((computeDate.getDay() in [0,6] && Holiday.findByHdate(computeDate))||(computeDate.getDay() in 1..5 && !Holiday.findByHdate(computeDate)))
        icount++
      computeDate++
    }
    icount
  }

  static Integer computeDateIntervalWorkDays(Date _start, Date _finish){
    if (!_start||!_finish) return 0
    Integer icount = 0
    while(_start<_finish){
      if ((_start.getDay() in [0,6] && Holiday.findByHdate(_start))||(_start.getDay() in 1..5 && !Holiday.findByHdate(_start)))
        icount++
      _start++
    }
    icount
  }

  static Date getNextWorkedDate(_initialdate){
    return getNextWorkedDate(_initialdate,Tools.getIntVal(Dynconfig.findByName('cashzakaz.nextworkingday.interval')?.value,2))
  }

  static Date getNextWorkedDate(Date _initialdate, Integer _interval){
    if (!_initialdate) return null
    def dStartdate = _initialdate.clone()
    while(_interval>0){
      dStartdate++
      if ((dStartdate.getDay() in [0,6] && Holiday.findByHdate(dStartdate))||(dStartdate.getDay() in 1..5 && !Holiday.findByHdate(dStartdate)))
        _interval--
    }
    dStartdate
  }

  static Date getPreviousWorkedDate(){
    return getPreviousWorkedDate(new Date())
  }

  static Date getPreviousWorkedDate(int _interval){
    return getPreviousWorkedDate(new Date(), _interval)
  }

  static Date getPreviousWorkedDate(Date _initialdate){
    return getPreviousWorkedDate(_initialdate,1)
  }

  static Date getPreviousWorkedDate(Date _initialdate, Integer _interval){
    if (!_initialdate) return null
    def dStartdate = _initialdate.clone()
    while(_interval>0){
      dStartdate--
      if ((dStartdate.getDay() in [0,6] && Holiday.findByHdate(dStartdate))||(dStartdate.getDay() in 1..5 && !Holiday.findByHdate(dStartdate)))
        _interval--
    }
    dStartdate
  }

  static BigDecimal toFixed(BigDecimal _value, Integer precision){
    Long divider = Math.pow(10d,precision.toDouble()).toLong()
    return new BigDecimal(Math.rint(_value * divider).toBigInteger(),precision)
  }

  static ArrayList getXlsTableHeaderStyle(Integer size){
    if(size<=0) return null
    else if (size==1) return [[wrap:true,borderTop:'THICK',borderLeft:'THICK',borderRight:'THICK',borderBottom:'MEDIUM']]
    else {
      def styles = [[wrap:true,borderTop:'THICK',borderLeft:'THICK',borderRight:'THIN',borderBottom:'MEDIUM']]
      while(--size>1) {
        styles << [wrap:true,borderTop:'THICK',borderLeft:'THIN',borderRight:'THIN',borderBottom:'MEDIUM']
      }
      styles << [wrap:true,borderTop:'THICK',borderLeft:'THIN',borderRight:'THICK',borderBottom:'MEDIUM']
      return styles
    }
  }

  static ArrayList getXlsTableFirstLineStyle(Integer size){
    if(size<=0) return null
    else if (size==1) return [[wrap:false,borderTop:'MEDIUM',borderLeft:'THICK',borderRight:'THICK',borderBottom:'THIN']]
    else {
      def styles = [[wrap:false,borderTop:'MEDIUM',borderLeft:'THICK',borderRight:'THIN',borderBottom:'THIN']]
      while(--size>1) {
        styles << [wrap:false,borderTop:'MEDIUM',borderLeft:'THIN',borderRight:'THIN',borderBottom:'THIN']
      }
      styles << [wrap:false,borderTop:'MEDIUM',borderLeft:'THIN',borderRight:'THICK',borderBottom:'THIN']
      return styles
    }
  }

  static ArrayList getXlsTableLastLineStyle(Integer size){
    if(size<=0) return null
    else if (size==1) return [[wrap:false,borderTop:'THIN',borderLeft:'THICK',borderRight:'THICK',borderBottom:'THICK']]
    else {
      def styles = [[wrap:false,borderTop:'THIN',borderLeft:'THICK',borderRight:'THIN',borderBottom:'THICK']]
      while(--size>1) {
        styles << [wrap:false,borderTop:'THIN',borderLeft:'THIN',borderRight:'THIN',borderBottom:'THICK']
      }
      styles << [wrap:false,borderTop:'THIN',borderLeft:'THIN',borderRight:'THICK',borderBottom:'THICK']
      return styles
    }
  }

  static ArrayList getXlsTableLineStyle(Integer size){
    if(size<=0) return null
    else if (size==1) return [[wrap:false,borderTop:'THIN',borderLeft:'THICK',borderRight:'THICK',borderBottom:'THIN']]
    else {
      def styles = [[wrap:false,borderTop:'THIN',borderLeft:'THICK',borderRight:'THIN',borderBottom:'THIN']]
      while(--size>1) {
        styles << [wrap:false,borderTop:'THIN',borderLeft:'THIN',borderRight:'THIN',borderBottom:'THIN']
      }
      styles << [wrap:false,borderTop:'THIN',borderLeft:'THIN',borderRight:'THICK',borderBottom:'THIN']
      return styles
    }
  }
}