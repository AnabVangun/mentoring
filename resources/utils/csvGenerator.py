import csv
import random

names = ['Abel','Achile','Aadam','Adem','Adrien','Aiden','Aimen','Aioub','Ahlan','Alban','Alessio','Alex','Alexandre','Alexis','Ali','Ahamed','Amine','Amir','Amauri','Amza','Anas','Andrea','Anis','Anthoine','Anthoni','Anthonin','Armand','Aaron','Arsene','Arthur','Axel','Baptiste','Basile','Bastien','Benjamin','Bilal','Brian','Camile','Charles','Charli','Clement','Come','Corenthin','Daniel','David','Diego','Dhylan','Djibril','Daurian','Edden','Edouard','Elias','Eli','Elio','Eliot','Emanuel','Emile','Enry','Enzho','Erwan','Esteban','Etan','Evahn','Ewen','Ezio','Fares','Felix','Florian','Gabin','Gabriel','Gael','Gaetan','Gaspard','Giani','Gauthier','Guilaume','Hyacine','Ian','Hyanis','Yassine','Hibrahim','Idriss','Hilan','Hylian','Ilias','Hylies','Imraan','Imrane','Ioan','Younes','Youssef','Isaac','Ismael',"Isma'il",'Hissa','Jean','Jeremi','Jhoan','Joshua','Jordan','Josef','Jules','Julian','Julien','Kahis','Keelian','Kenzo','Keevin','Khylian','Leandre','Leandro','Lehny','Leho','Leon','Leonard','Lhiam','Lilian','Lino','Livio','Loan','Laugan','Loic','Laurenzo','Lauris','Louis','Louka','Luca','Lucas','Lucien','Luka','Mae','Mael','Mairon','Malho','Mahlone','Marcel','Marceau','Marin','Marius','Martin','Mateo','Mathias','Mathieu','Mathis','Maxence','Maxime','Meddhi','Melvin','Milan','Milo','Mohamed','Morgan','Moussa','Nael',"Na'il",'Nahim','Nassim','Natan','Natanael','Nicolas','Ninho','Nhoa','Noahm','Nhoé','Naulan','Auguste','Augustin','Oscar','Howen','Pablo','Pierre','Paul','Quentin','Rafael','Rahyan','Rahyane','Remi','Riad','Rhyan','Robin','Romain','Romeo','Rubben','Sacha','Sami','Sammuel','Sandro','Simon','Soan','Saufiane','Swan','Taho','Tehau','Thiago','Thibault','Thimeo','Thimote','Titouan','Thom','Thomas','Thony','Tristan','Huggo','Ulisse','Valentin','Victor','Wassim','Wilhiam','Zakaria','Adele','Agate','Aaya','Aicha','Albane','Aalia','Alice','Alicia','Alissa','Alix','Amandine','Ambre','Amelia','Ameli','Amina','Amira','Ahna','Anae','Anaele','Anais','Andrea','Annouk','Apauline','Aria','Asma','Assia','Ahava','Axele','Cali','Camelia','Camille','Candice','Cappucine','Carla','Cassandre','Celeste','Celhia','Chahima','Chahina','Chana','Charlie','Charline','Charlotte','Chloe','Clara','Clarisse','Clea','Clemence','Clementine','Clhoe','Cauline','Constance','Diane','Dina','Eden','Ela','Elea','Elena','Eleonore','Elhya','Eliana','Eli','Elina','Eline','Elisa','Elise','Eloise','Elsa','Ema','Emi','Emili','Ennora','Eeva','Fathima','Faustine','Gabriele','Garance','Giulia','Yasmine','Hilyana','Hinaya','Ines','Iris','Jaade','Jahna','Jeane','Jena','Joana','Joy','Josefine','Julia','Julie','Juliette','Justine','Kadija','Kelhia','Kenza','Khiara','Lahyana','Lali','Lahna','Lara','Lea','Leana','Leane','Leeya','Leila','Lehina','Leena','Leoni','Lhya','Liana','Lila','Lilia','Lili','Lili-rose','Li-lou','Lhyna','Line','Lisa','Lise','Lison','Livia','Laula','Laura','Laurine','Lou','Lou-anne','Louisa','Louise','Lhouna','Luci','Lucile','Lluna','Maddi','Mae-lee','Maelia','Mae-li','Maeline','Mae-lys','Maeva','Mahia','Mahissa','Maiween','Mahalia','Manel','Mannon','Margaut','Margaux','Maria','Mariam','Mari','Marilou','Marine','Marion','Marwa','Mathilde','Mehlia','Melina','Meline','Melissa','Meriem','Mia','Mila','Miriam','Morgane','Naele','Nahia','Nahila','Nahomi','Nehila','Nehlia','Nina','Ninon','Noeli','Noemi','Naura','Nour','Auceane','Olivia','Pauline','Romane','Romi','Rose','Roxane','Sacha','Saffia','Salma','Salome','Saana','Saara','Selena','Selma','Sirine','Sofia','Sofi','Soline','Stela','Suzane','Tahis','Tahlia','Tea','Tess','Tessa','Valentine','Victoire','Victoria','Zeli','Zhoé']
domains = ["FINANCE","DATA","ENERGIE","NUCLEAIRE","STRATEGIE","MATHS",
           "INTELLIGENCE_ARTIFICIELLE","SCIENCE","CONSEIL","ECONOMIE",
           "POLITIQUE","AERONAUTIQUE","SPATIAL","DEFENSE","DURABLE","MANAGEMENT"]
motivations = ["CONFIDENT","RESEAUTER","ENTREPRISE","ADMINISTRATION",
               "RECHERCHE","ENTREPRENEURIAT"]
def listPicker(entries):
    return random.choice(entries)
def rowBuilder(parrain):
    return ['2021/08/17 3:32:41 PM UTC+2', 
            listPicker(names), listPicker(names), 
            (random.randrange(1970,2010) if parrain else 2020)] \
        + [";".join({listPicker(domains) for _ in range(random.randrange(1,5))})] \
        + [";".join({listPicker(motivations) for _ in range(random.randrange(1,3))})]\
        + [listPicker([True, False])]
def writeFile(path, nLines, parrain):
	with open(path, "w", newline="", encoding='utf-8') as csvfile:
		writer = csv.writer(csvfile, dialect=csv.unix_dialect)
		writer.writerow(['Horodateur', 'Prénom', 'Nom', 'Promotion', 
                   "Activités et métiers","Motivation","Anglais"])
		for _ in range(nLines):
			writer.writerow(rowBuilder(parrain))