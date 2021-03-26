from django.shortcuts import render, redirect, get_object_or_404
from django.http import HttpResponse
from keygenApp.models import Key
from keygenApp.forms import KeyForm
from keygenApp.utils import StringGenerator
import datetime
from dateutil.relativedelta import *
import json 
from django.core.exceptions import ObjectDoesNotExist
from django.views.decorators.csrf import csrf_exempt


    
def index(request): 
    context = {}

    form = KeyForm(request.POST or None)

    context['form'] = form 
    context["keys"] = Key.objects.all()

    
    return render(request,'generator.html',context)


@csrf_exempt
def save(request):
   
    context = {}
    
    #form = KeyForm(request.POST or None)
    form = KeyForm(request.POST)
    
    print(form.is_valid()) 
    print(request.POST)
    result = "" 
    
    if form.is_valid() : 
        k = form.save(commit=False)
        
        # avoir la date actuelle et la stocker
        date = datetime.date.today()
        k.create_date =  date
        # stocker la date d'expiration ( 6 mois )
        date = date + datetime.timedelta(6*365/12)
        k.expire_date = date
        
        k.save() 
        
    else:
            print(form.errors)
            return HttpResponse(json.dumps({'for':'error','message':form.errors}),content_type="application/json")
         
    context['form'] = form 
    
    # retourner une liste json des entités de la bdd + celle ajoutée
    #results = [ob.as_json() for ob in Key.objects.all()]
    result = Key.objects.get(key=k.key).as_json() 
    return HttpResponse(json.dumps(result), content_type="application/json")
 
    #return HttpResponse(context)


# view pour supprimer une clé

@csrf_exempt
def delete(request) : 
    
    
    if request.method == 'POST' : 
        # recevoir la clé et la supprimer
        try :
            key_from_request = request.POST.get("key")
        except ObjectDoesNotExist as DoesNotExist :
            return HttpResponse(json.dumps({'message':"key doesn't exist"},content_type="application/json"))
        
        #chercher la clé
        k = Key.objects.get(key=key_from_request)
        k.delete()
    
    # retourner une liste json des entrés de la bdd moins celle supprimée
    results = [ob.as_json() for ob in Key.objects.all()]
    return HttpResponse(json.dumps(results), content_type="application/json")


# view pour générer une clé
def generate(request) :

    context = {}
    
    
    # faire appel àa la fonction generate_key de la classe StringGenerator
    generated = StringGenerator().generate_key() 
    return HttpResponse(generated)


# view pour toutes les clés


#retourne toutes les clés

def get_all(request) : 
    
    context = {}
    
    #context["keys"] = Key.objects.all()

        
    #return HttpResponse(context)
    
     # retourner une liste json des entrés de la bdd
    results = [ob.as_json() for ob in Key.objects.all()]
    return HttpResponse(json.dumps(results), content_type="application/json")


# view pour des détails sur une clé recherchée

def get_key(request,key) : 
    
    context = {}
    
    context["key"] = Key.objects.get(key=key)
    
    return HttpResponse(context)

# changer clé utilisée ou non 
def update(request):
    
    if request.method == 'POST' : 
        # recevoir la clé à modifier
        try :
            key_from_request = request.POST.get("key")
        except ObjectDoesNotExist as DoesNotExist :
            return HttpResponse(json.dumps({'message':"key doesn't exist"},content_type="application/json"))
        
        #chercher la clé
        k = Key.objects.get(key=key_from_request)
        k.used = request.POST.get("used")
        k.save() 
        
    
    # retourner une liste json des entrés de la bdd 
    results = [ob.as_json() for ob in Key.objects.all()]
    return HttpResponse(json.dumps(results), content_type="application/json")


#about
def about(request):
    context = {}
    
    return render(request,'about.html',context)
