import random

header = """<?xml version="1.0" encoding="UTF-8"?>
<map version="1.10" tiledversion="1.10.2" orientation="orthogonal" renderorder="right-down" width="500" height="20" tilewidth="32" tileheight="32" infinite="0" nextlayerid="3" nextobjectid="100">
 <properties>
  <property name="gravity" type="float" value="-1600"/>
  <property name="levelName" value="Level 1 Extended"/>
  <property name="scrollSpeed" type="float" value="350"/>
 </properties>
 <tileset firstgid="1" name="tiles" tilewidth="32" tileheight="32" tilecount="1" columns="1">
  <image source="../libgdx.png" width="32" height="32"/>
 </tileset>
 <layer id="1" name="Tile Layer 1" width="500" height="20">
  <data encoding="csv">
"""

footer = """
</data>
 </layer>
 <objectgroup id="2" name="Objects">
  <object id="1" name="PlayerStart" type="PlayerStart" x="96" y="416" width="32" height="32"/>
  <object id="2" name="Ground" type="Solid" x="0" y="448" width="16000" height="32"/>
"""

# Generate CSV data (500 * 20 zeros)
data = ""
for i in range(20):
    line = ",".join(["0"] * 500)
    if i < 19:
        line += ",\n"
    data += line

# Generate Objects
objects = ""
current_x = 800
obj_id = 10

while current_x < 15000:
    # Randomly choose obstacle type
    r = random.random()
    if r < 0.4:
        # Spike
        objects += f'  <object id="{obj_id}" name="Spike" type="Spike" x="{current_x}" y="416" width="32" height="32"/>\n'
    elif r < 0.7:
        # Block
        objects += f'  <object id="{obj_id}" name="Block" type="Solid" x="{current_x}" y="384" width="32" height="64"/>\n'
    else:
        # Gap (just no obstacle, maybe a small platform)
        # For now just simple blocks and spikes
        objects += f'  <object id="{obj_id}" name="Block" type="Solid" x="{current_x}" y="352" width="64" height="96"/>\n'
    
    obj_id += 1
    current_x += random.randint(400, 800)

# End Flag
objects += f'  <object id="{obj_id}" name="EndFlag" type="EndFlag" x="15500" y="0" width="32" height="1000"/>\n'

objects += " </objectgroup>\n</map>"

with open("assets/maps/level1.tmx", "w") as f:
    f.write(header + data + footer + objects)
