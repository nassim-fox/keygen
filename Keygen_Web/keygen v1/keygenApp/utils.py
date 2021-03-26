


import string   
import random   



class StringGenerator():
    
    def generate_key(self) : 
        S = 16
        ran = ''.join(random.choices(string.ascii_uppercase + string.digits, k = S))    
        print("generated key : " + str(ran)) 
        return ran 