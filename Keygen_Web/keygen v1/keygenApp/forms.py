
from django import forms
from keygenApp.models import Key


class KeyForm(forms.ModelForm): 
    class Meta : 
        model = Key
        fields = ["key",]#"__all__"
        widgets = { 
            'key': forms.TextInput( attrs={ 'class': 'form-control', 'id': 'key_field', 'placeholder': 'appuyer sur générer...', 'readonly':'readonly'} ),
        }