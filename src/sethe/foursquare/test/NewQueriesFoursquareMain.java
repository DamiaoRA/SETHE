package sethe.foursquare.test;

public class NewQueriesFoursquareMain {
/*
	--> só poi + 1 aspecto
	(1) trajetórias que param na Bryant Park e logo depois na Madison Square e o dia estava claro.

	SELECT trajectory_id, trajectory_value
	FROM foursquare.tb_poi where trajectory_value ~* 'Bryant Park;Madison Square';

	--> só categoria + 2 aspectos
	(2) trajetórias que pararam em uma residência e o clima estava nublado, logo depois pararam em um restaurante ou shopping bem avaliado e depois em um colégio
	bem avaliado = rating > 7

	SELECT trajectory_id, trajectory_value
	FROM foursquare.tb_category where trajectory_value ~* 'Residence;Food;College University';

    --> poi + cat + algum tempo depois
	(3) trajetórias que pararam em um colégio e depois em um lugar de recreação bem avaliado ou vice-versa.

	--> poi + cat + 3 aspectos
	(4) trajetórias que pararam no Central Park em uma sexta-feira nublada, logo depois usaram metrô para uma festa noturna bem avaliada.
	bem avaliada = rating >= 7

	SELECT trajectory_id, trajectory_value
	FROM foursquare.tb_category where trajectory_value ~* 'recreation;(\w* )*Transport;Nightlife';

	--> poi+aspectos + terminam
	(5) trajetórias que passaram pela Madison Square em um sábado ensolarado e terminaram na Times square.

	SELECT trajectory_id, trajectory_value
	FROM foursquare.tb_poi where trajectory_value ~* 'Madison Square'
	and
	trajectory_value ~* 'Times square(  \w*)*$'
	
	--> poi+cat+aspectos+terminam
	(6) trajetórias que pararam na Midtown Comics e terminam em um restaurante caro ou em um shopping.

	--> cat+aspectos+começam
	(7) trajetórias que começam na residencia em uma segunda-feira chuvosa, e logo em seguida param em um Colégio, Universidade ou trabalho.
	(8) trajetórias que começam no trabalho ou universidade, termina em uma residência e o clima está sempre claro durante toda a trajetória.

	--> começa na categoria+termina em um poi+aspectos
	(9) trajetória que começa no shopping ou residência, logo em seguinda para em um local de preço e rating alto e termina no Chase Bank.

	--> começa em um poi, termina na categoria, aspectos
	(10)trajetórias que começam em um sábado na Times Square, algum tempo depois para em um local bem avaliado e logo em seguida 
	termina em uma residencia ou restaurante. 

	preço
	rating
	tempo
	proximidade (logo depois,  algum tempo depois)
	começam e terminam
	
Residence
Food
Travel & Transport
Professional & Other Places
Shop & Service
Outdoors & Recreation
College & University
Arts & Entertainment
Nightlife Spot
Event
	*/
}























