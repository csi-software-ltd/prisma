import org.codehaus.groovy.grails.commons.ConfigurationHolder

class ParseService {
  def parseCSVBankFile(fileCSV){	  
    if (fileCSV.empty){
      //throw new Exception ('No file')
      return [result:'No file',code:1]
    }  
    //if (fileCSV.getContentType() != "text/comma-separated-values"){
    if (fileCSV.getContentType() != "text/csv" && fileCSV.getContentType() != "application/vnd.ms-excel"){    
      //throw new Exception ('Not supported file type')
      return [result:'Not supported file type',code:2]
    }
    def result = [complete:0,total:0,notimport:[],notneededsize:[]]    
    def lsLines = fileCSV.getInputStream().readLines('utf-8')
    result.total=lsLines.size()
    
    if(!(fileCSV.originalFilename.toLowerCase().contains('bnkedit') || fileCSV.originalFilename.toLowerCase().contains('bnkseek') || fileCSV.originalFilename.toLowerCase().contains('bnkdel'))){             
      return [code:4]
    }
    
    Bank.withTransaction { status ->
      lsLines.eachWithIndex{ item, idx ->
        def oSavepoint = status.createSavepoint()
        def lsData=item.split(';',-1)               
      
        try {                             
          if(!(fileCSV.originalFilename.toLowerCase().contains('bnkedit') && (lsData.size()>=2) || (fileCSV.originalFilename.toLowerCase().contains('bnkseek') && lsData.size()==28) || (fileCSV.originalFilename.toLowerCase().contains('bnkdel') && lsData.size()==27))){
            result.notneededsize<< idx+1
            throw new Exception ('adding Bank error')                    
          }                    
          if(fileCSV.originalFilename.toLowerCase().contains('bnkseek')){
            if(idx){
              def oBank=Bank.get(lsData[12])
            
              if(!oBank)
                oBank=new Bank()
      
              oBank.csiSetCSVBankFromBNKSEEK(lsData).save(flush:true,failOnError:true)                             
           
              result.complete++
              status.releaseSavepoint(oSavepoint)
            }
          }else if(fileCSV.originalFilename.toLowerCase().contains('bnkdel')){
            if(idx){
              def oBank=Bank.get(lsData[12])
            /* only edit!
              if(!oBank)
                oBank=new Bank()
            */if(oBank){ 
                oBank.csiSetCSVBankFromBNKDEL(lsData).save(flush:true,failOnError:true)                                       
                result.complete++
                status.releaseSavepoint(oSavepoint)
              }  
            }          
          }else if(fileCSV.originalFilename.toLowerCase().contains('bnkedit')){
            def oBank=Bank.get(lsData[0])
            if(!oBank)
              oBank=new Bank()
      
            oBank.csiSetCSVBank(lsData).save(flush:true,failOnError:true)                             
          
            result.complete++
            status.releaseSavepoint(oSavepoint)          
          }
        } catch(Exception e) {
          log.debug('error on create Bank in parseCSVBankFile '+idx+'\n'+e.toString())
          result.notimport << idx+1
          status.rollbackToSavepoint(oSavepoint)
        }
      }
    }    
    def hsRes=[:]   
    hsRes.result=result.complete.toString() + ' of ' + result.total + ' was imported. ' + (result.notimport?('Not imported lines are: '+result.notimport.toString()):'')
    hsRes.code=3
    hsRes.complete=result.complete
    hsRes.total=result.total
    
    if(fileCSV.originalFilename.toLowerCase().contains('bnkseek') || fileCSV.originalFilename.toLowerCase().contains('bnkdel'))
      hsRes.total--
      
    hsRes.notimport=result.notimport 
    hsRes.notneededsize=result.notneededsize
    return hsRes    
  } 
/////////////////////////////////////////////////////////////////////////////
  def parseCSVOkvedFile(fileCSV){	  
    if (fileCSV.empty){
      //throw new Exception ('No file')
      return [result:'No file',code:1]
    }  
    //if (fileCSV.getContentType() != "text/comma-separated-values"){
    if (fileCSV.getContentType() != "text/csv"){    
      //throw new Exception ('Not supported file type')
      return [result:'Not supported file type',code:2]
    }
    def result = [complete:0,total:0,notimport:[],notneededsize:[]]    
    def lsLines = fileCSV.getInputStream().readLines('windows-1251')
    result.total=lsLines.size()
    
    Okved.withTransaction { status ->
      lsLines.eachWithIndex{ item, idx ->
        def oSavepoint = status.createSavepoint()
        def lsData=item.split(';',-1)
      
        try {         
          if(lsData.size()!=5){
            result.notneededsize<< idx+1
            throw new Exception ('adding Okved error')                    
          }
          
          def oOkved=Okved.get(lsData[0])
          if(!oOkved)
            oOkved=new Okved()
      
          oOkved.csiSetCSVOkved(lsData).save(flush:true,failOnError:true)                             
          
          result.complete++
          status.releaseSavepoint(oSavepoint)          
        } catch(Exception e) {
          log.debug('error on create Okved in parseCSVOkvedFile '+idx+'\n'+e.toString())
          result.notimport << idx+1
          status.rollbackToSavepoint(oSavepoint)
        }
      }
    }    
    def hsRes=[:]
    hsRes.result=result.complete.toString() + ' of ' + result.total + ' was imported. ' + (result.notimport?('Not imported lines are: '+result.notimport.toString()):'')
    hsRes.code=3
    hsRes.complete=result.complete
    hsRes.total=result.total
    hsRes.notimport=result.notimport 
    hsRes.notneededsize=result.notneededsize
    return hsRes    
  }
//////////////////////////////////////////////////////////////////////////////////////  
  def parseCSVOktmoFile(fileCSV){	  
    if (fileCSV.empty){
      //throw new Exception ('No file')
      return [result:'No file',code:1]
    }  
    //if (fileCSV.getContentType() != "text/comma-separated-values"){
    if (fileCSV.getContentType() != "text/csv"){    
      //throw new Exception ('Not supported file type')
      return [result:'Not supported file type',code:2]
    }
    def result = [complete:0,total:0,notimport:[],notneededsize:[]]    
    def lsLines = fileCSV.getInputStream().readLines('windows-1251')
    result.total=lsLines.size()
    
    Oktmo.withTransaction { status ->
      lsLines.eachWithIndex{ item, idx ->
        def oSavepoint = status.createSavepoint()
        def lsData=item.split(';',-1)
      
        try {         
          if(lsData.size()!=3){
            result.notneededsize<< idx+1
            throw new Exception ('adding Oktmo error')                    
          }          
          def oOktmo=Oktmo.get(lsData[0])
          if(!oOktmo)
            oOktmo=new Oktmo()
      
          oOktmo.csiSetCSVOktmo(lsData).save(flush:true,failOnError:true)                             
          
          result.complete++
          status.releaseSavepoint(oSavepoint)          
        } catch(Exception e) {
          log.debug('error on create Oktmo in parseCSVOktmoFile '+idx+'\n'+e.toString())
          result.notimport << idx+1
          status.rollbackToSavepoint(oSavepoint)
        }
      }
    }    
    def hsRes=[:]
    hsRes.result=result.complete.toString() + ' of ' + result.total + ' was imported. ' + (result.notimport?('Not imported lines are: '+result.notimport.toString()):'')
    hsRes.code=3
    hsRes.complete=result.complete
    hsRes.total=result.total
    hsRes.notimport=result.notimport 
    hsRes.notneededsize=result.notneededsize
    return hsRes    
  }
//////////////////////////////////////////////////////////////////////////////////////  
  def parseCSVKbkFile(fileCSV){	  
    if (fileCSV.empty){
      //throw new Exception ('No file')
      return [result:'No file',code:1]
    }  
    //if (fileCSV.getContentType() != "text/comma-separated-values"){
    if (fileCSV.getContentType() != "text/csv"){    
      //throw new Exception ('Not supported file type')
      return [result:'Not supported file type',code:2]
    }
    def result = [complete:0,total:0,notimport:[],notneededsize:[]]    
    def lsLines = fileCSV.getInputStream().readLines('windows-1251')
    result.total=lsLines.size()
    
    Kbk.withTransaction { status ->
      lsLines.eachWithIndex{ item, idx ->
        def oSavepoint = status.createSavepoint()
        def lsData=item.split(';',-1)
      
        try {         
          if(lsData.size()!=7){
            result.notneededsize<< idx+1
            throw new Exception ('adding Kbk error')                    
          }
          def oKbk=Kbk.findByName(lsData[0])
          if(!oKbk)
            oKbk=new Kbk()
      
          oKbk.csiSetCSVKbk(lsData).save(flush:true,failOnError:true)                             
          
          result.complete++
          status.releaseSavepoint(oSavepoint)          
        } catch(Exception e) {
          log.debug('error on create Kbk in parseCSVKbkFile '+idx+'\n'+e.toString())
          result.notimport << idx+1
          status.rollbackToSavepoint(oSavepoint)
        }
      }
    }    
    def hsRes=[:]
    hsRes.result=result.complete.toString() + ' of ' + result.total + ' was imported. ' + (result.notimport?('Not imported lines are: '+result.notimport.toString()):'')
    hsRes.code=3
    hsRes.complete=result.complete
    hsRes.total=result.total
    hsRes.notimport=result.notimport
    hsRes.notneededsize=result.notneededsize    
    return hsRes    
  }
//////////////////////////////////////////////////////////////////////////////////////  
  def parseCSVTaxinspectionFile(fileCSV){	  
    if (fileCSV.empty){
      //throw new Exception ('No file')
      return [result:'No file',code:1]
    }  
    //if (fileCSV.getContentType() != "text/comma-separated-values"){
    if (fileCSV.getContentType() != "text/csv"){    
      //throw new Exception ('Not supported file type')
      return [result:'Not supported file type',code:2]
    }
    def result = [complete:0,total:0,notimport:[],notneededsize:[]]    
    def lsLines = fileCSV.getInputStream().readLines('windows-1251')
    result.total=lsLines.size()
    
    Taxinspection.withTransaction { status ->
      lsLines.eachWithIndex{ item, idx ->
        def oSavepoint = status.createSavepoint()
        def lsData=item.split(';',-1)                              
      
        try {                                            
          if(lsData.size()!=5){
            result.notneededsize<< idx+1
            throw new Exception ('adding Taxinspection error')            
          }
        
          def oTaxinspection=Taxinspection.get(lsData[0])
          if(!oTaxinspection)
            oTaxinspection=new Taxinspection()
      
          oTaxinspection.csiSetCSVTaxinspection(lsData).save(flush:true,failOnError:true)                             
          
          result.complete++
          status.releaseSavepoint(oSavepoint)          
        } catch(Exception e) {
          log.debug('error on create Taxinspection in parseCSVTaxinspectionFile '+idx+'\n'+e.toString())
          result.notimport << idx+1
          status.rollbackToSavepoint(oSavepoint)
        }
      }
    }    
    def hsRes=[:]
    hsRes.result=result.complete.toString() + ' of ' + result.total + ' was imported. ' + (result.notimport?('Not imported lines are: '+result.notimport.toString()):'')
    hsRes.code=3
    hsRes.complete=result.complete
    hsRes.total=result.total
    hsRes.notimport=result.notimport 
    hsRes.notneededsize=result.notneededsize
    return hsRes    
  }

  def parseKreditpaymentsFile(_file){
    def result = [errorcode:[],preparedData:null]
    if(!_file.originalFilename) {
      result.errorcode << 1
      return result
    }
    try {
      result.preparedData = _file.getInputStream().readLines('windows-1251').collect{ line ->
        def data = line.split(';')
        [kreditpayment_paydate:Date.parse('dd.MM.yyyy', data[0]),summa:data[1].replace(',','.').toBigDecimal(),summaperc:data[2].replace(',','.').toBigDecimal(),paidmonth:data[3].toInteger()]
      }
    } catch(Exception e) {
      result.errorcode << 2
    }
    result
  }

  def parseLoanpaymentsFile(_file){
    def result = [errorcode:[],preparedData:null]
    if(!_file.originalFilename) {
      result.errorcode << 1
      return result
    }
    try {
      result.preparedData = _file.getInputStream().readLines('windows-1251').collect{ line ->
        def data = line.split(';')
        [loanpayment_paydate:Date.parse('dd.MM.yyyy', data[0]),summa:data[1].replace(',','.').toBigDecimal(),summaperc:data[2].replace(',','.').toBigDecimal(),paidmonth:data[3].toInteger()]
      }
    } catch(Exception e) {
      result.errorcode << 2
    }
    result
  }

  def parseLizingpaymentsFile(_file){
    def result = [errorcode:[],preparedData:null]
    if(!_file.originalFilename) {
      result.errorcode << 1
      return result
    }
    try {
      result.preparedData = _file.getInputStream().readLines('windows-1251').collect{ line ->
        def data = line.split(';')
        [planpayment_paydate:Date.parse('dd.MM.yyyy', data[0]),summa:data[1].replace(' ','').replace(',','.').toBigDecimal()]
      }
    } catch(Exception e) {
      result.errorcode << 2
    }
    result
  }

  def parseBuhReportFile(_file){
    def result = [errorcode:[],preparedData:null,reportDate:null]
    if(!_file.originalFilename) {
      result.errorcode << 1
      return result
    }
    def lsLines = _file.getInputStream().readLines('windows-1251')
    try {
      result.reportDate = Date.parse('MMMMM yy',lsLines.head().split(';')[2])
      if (Salaryreport.findByMonthAndYearAndSalarytype_id(result.reportDate.getMonth()+1,result.reportDate.getYear()+1900,2)) {
        result.errorcode << 3
        return result
      }
    } catch(Exception e) {
      log.debug(e.toString())
      result.errorcode << 2
      return result
    }

    try {
      result.preparedData = lsLines.tail().tail().collect{ line ->
        def data = line.split(';',-1)
        if (data[0]!='')
          [companyname:data[0],companyinn:data[1],region:data[2],fio:data[3],snils:data[4],position:data[5],fullsalary:(data[6]?data[6].replace(' ','').replace(',','.').toBigDecimal():0g),netsalary:(data[7]?data[7].replace(' ','').replace(',','.').toBigDecimal():0g),debtsalary:(data[8]?data[8].replace(' ','').replace(',','.').toBigDecimal():0g),ndfl:(data[9]?data[9].replace(' ','').replace(',','.').toBigDecimal():0g),debtndfl:0,fss_tempinvalid:(data[10]?data[10].replace(' ','').replace(',','.').toBigDecimal():0g),debtfss_tempinvalid:0,fss_accident:(data[11]?data[11].replace(' ','').replace(',','.').toBigDecimal():0g),debtfss_accident:0,ffoms:(data[12]?data[12].replace(' ','').replace(',','.').toBigDecimal():0g),debtffoms:0,pf:(data[13]?data[13].replace(' ','').replace(',','.').toBigDecimal():0g),debtpf:0]
        else null
      } - [null]
    } catch(Exception e) {
      result.errorcode << 2
    }
    result
  }

  def parseTaxReportFile(_file){
    def result = [errorcode:[],preparedData:null,reportDate:null,tax:null]
    if(!_file.originalFilename) {
      result.errorcode << 1
      return result
    }
    def lsLines = _file.getInputStream().readLines('windows-1251')
    try {
      result.reportDate = Date.parse('MMMMM yy',lsLines.head().split(';')[0])
      result.tax = Tax.findByShortnameAndIdGreaterThanEquals(lsLines.head().split(';')[1],6)
      if (!result.tax) {
        result.errorcode << 3
        return result
      }
    } catch(Exception e) {
      log.debug(e.toString())
      result.errorcode << 2
      return result
    }

    try {
      result.preparedData = lsLines.tail().tail().tail().tail().collect{ line ->
        def data = line.split(';',-1)
        if (data[1]!='')
          [companyname:data[0],companyinn:data[1],taxsumma:(data[result.tax.columnnumber]?data[result.tax.columnnumber].replace(' ','').replace(',','.').toBigDecimal():0g)]
        else null
      } - [null]
    } catch(Exception e) {
      result.errorcode << 2
    }
    result
  }

}