from django.db import models



class Key(models.Model) : 
    key = models.CharField(max_length=20,primary_key=True)
    create_date = models.DateTimeField(auto_now=True)
    expire_date = models.DateTimeField() 
    used = models.BooleanField(default=False)
    
    def as_json(self):
        return dict(
            key=self.key, create_date=self.create_date.strftime('%d/%m/%Y'),
            expire_date=self.expire_date.strftime('%d/%m/%Y'),
            used=self.used)