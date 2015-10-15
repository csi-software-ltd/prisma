class Valuta {    
  
  static constraints = {	
  }
  static mapping = {
    version false
  }
  Integer id
  Integer modstatus
  Integer regorder
  String code
  String name
  String shortname
  String symbol    

  String toString() {"${this.code}" }  
  
}
