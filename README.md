MudokuWithAgents
================

Mudoku with Agents - Documentation
Alejandro Yuste
Tampere University of Technology
alejandro.yuste@tut.fi
February 6, 2013

1 Introduction
The main aim of this tool is to simulate how an Open Source Community
(OSC) works. The development was based on the Mudoku tool, created to
solve a Sudoku problem by multiple users in a server-client model. This
application was chosen because the design was the appropriated and also
because the Sudoku problem allows the simulation of a system with three
dierent subsystems who have to work together to arrive a nal good solu-
tion.

In this way, in Section 2 will be explained how an OSC has been simulated,
then in Section 3 the principal dierences between Mudoku and Mudoku with
Agents, in Section 4 a brief description of the new components and nally in
Section 5 how to run the new tool to do not miss any characteristic.

2 Simulating an OSC with a Sudoku

To simulate an OSC what it has been done is dene the behavior of the
dierent roles that can be found in a community with intelligent agents.
Then, the agents are launched in a controlled framework and is checked how
they are developing the project, in this case solving a Sudoku. Roles of an
OSC simulated:

 Passive User: This role is only applicable for the users. The only thing
they can do is observe the grid and how the others agents and users
are collaborating to solve a Sudoku.
 Contributor: This kind of agents or users will be who are going to add
new values to the grid.
 Bug Reporter: In this role also agents and users will be able to report
wrong values that have been contributed.
 Tester: Agents and users check contributed values to start voting ses-
sions for committing them.
 Committer: When there is a voting session this agents or users will be
the ones who can vote.
 Project Leader: An agent or user in this role can decide if a committed
value is accepted or rejected for the nal solution.

Thus, each cell of the grid has dierent states depending on its level of
development. At the beginning of the game all the cells are initialized by
the server or waiting a value. Then, when a random community is created,
the game starts. At this point the cells might have any of the follow states:
contributed, reported, committed, accepted or rejected. And it is dierenced
also for the kind of agent that has produced this state: Rows, Columns,
Square or Leader.

3 Differences between both tools
Mudoku with Agents took the design of Mudoku. The server-client model
was what we needed because permit us the creation of dierent proles of
clients that work with the same game located in the server. In this manner,
two proles/frameworks have been created: one to control the intelligent
agents and another one for real users that are also able to join the commu-
nity and try the dierent roles.
The library CHOCO, used in Mudoku to capture the mouse events and to
control the rules of the game, has been used only to get the mouse events. We
could not use it for the rules of the game because we needed the agents/users
commit mistakes as it happens in the reals OSC. For this reason, only an
agent with the role of a Project Leader, or an user, knows all the rules of the
game and all the other agents only knows the rules of one of the subsystems:
Rows, Columns or Squares.


3.1 Differences in the Server

The server is rather distinct. It is still being the place where the game is
initialized and who received all the messages with the actions of the clients.
However, it is not controlling if the actions are correct or not, it only keeps
the communication among all the clients connected. Also, a new protocol of
messages have been dened in order to take care of the new actions that can
be produced.

The new tool provides two modes of working:
 Text Mode: It is similar to the mudoku one. There is a console where
can be checked all the actions produced.
 Graphic Mode: It is the default mode. The framework is divided in
three parts: Above there is a legend with all the members connected
and the role of each one. In the middle on the left there are some
information about the game (correct values, number of contributions,
values rejected,...) and on the right a graphic "console" where can be
checked the last seven actions produced. Below there is the box for
the voting sessions. Every time there is a voting to decide if a value
will be committed or not in this box can be checked the vote of every
agent/user at the moment is received.

3.2 Differences in the Clients

How it has been commented, the new tool has two types of frameworks: one
for the intelligent agents and another one for the clients. But we can have
many of them runing at the same time with the same game.
 Agents: This framework allows choose how many intelligent agents
there will be in the community. The framework is divided in three
parts: on the top-left corner can be visualized the grid. On the bottom-
left there are two boxes to add agents to the community: the first one
permits add as many agents of one selected type as we want and the
second one create a random community with all the roles represented
with as many agents as we type. On the left there are information
boxes, a legend with the colors that appear on the grid and a box
to disconnect agents (their use is not recommended: it can produce
deadlock because of the using of threads + java monitors).
 Clients: This framework was thought with an educational purpose. The
users can join to the community, after a few questions, and play with
the dierent roles to learn how a community works. Depending on its
behavior the users will be able to reach determined roles, nalizing the
game on the project leader role. The only part in common that all the
roles have is the grid on the top-left corner.


4 New Components


Mudoku with agents is not only a tool that solves a Sudoku. As user, you can
join in a simulation of an OSC and learn not just how a project is developed;
you can participate to help to it.
4.1 Colors of the Grid
At the beginning, to understand what is happening in the grid could not be
easy, for this reason it has been tried to create a conguration of colors that
helps to get used with the tool. Every cell is formed by two dierent col-
ors, the background indicates which kind of agent or user has produced the
current state and the foreground, separated by an oval, indicates the level of
development. Those cells that have been set as a Bug Reported, Not Com-
mitted or value Rejected will have not number written and the foreground
is white, this would mean that a contributor can use them again to try to
complete the game.
When a voting session is active the agents are not allowed to produce actions,
because this could aect to the voting. However, the users can still playing
during the voting, this is because the game is stopped most of the time and
it could be annoying and boring for the users.

4.2 Types Of Agents


The dierent types of roles that have been simulated were explained. But it is
important to understand that every agent only works in a dened subsystem,
unless the leader role. This means than the agents only know a part of the
rules of the game and they depend on the others to get the nal solution.
How aect this to each role?
 Contributor: Every Agent only adds correct values in its subsystem.
Namely, a row agent only checks a value is going to add in the row it
bellows, never on the column or the square.
 Bug Reporter: A row agent that is checking a value only will test if it
is correct in the row, never on the columns or the square. Also, this
agent only will check values contributed by a columns or square agent;
because it knows that the values contributed by another row agent are
correct on the rows.
 Tester: As in the last cases, a row agent of this type only will test values
contributed by the other types of agents. In this manner, this agent
will ask for committing values are correct in two dierent subsystems,
increasing the possibilities of being committed.
 Committer: When there is a voting a Row Committer will test the
value only by rows, with a few possibilities to wrong the vote.
 Project Leader: An agent of this type knows all the rules, so its work
is accept or reject that values that have been committed and they have
a high possibility to be accepted.

4.3 Educational Purpose

The client's framework was thought with an educational purpose. In this
way, it is a kind of game in the whole game. An user will start as a Passive
User and its goal will be get promotions until it reaches the Project Leader
role.
When the user tries to join the community it will have to answer correctly
a number of questions about the Sudoku that can be selected before. Thus,
once the user is accepted to join the community it will be a contributor. In
this level the user has to contribute values until it reaches a determined num-
ber of contributions committed. The next level is the Bug Reporter, where
the user has to select incorrect contributions until it has the possibility to
get promotion. In the Tester level the user ask for committing values until
a determined number of them have been committed. When it reaches Com-
mitter it only will take part in the voting. Every time there is a voting the
user can vote to decide if a value has to be committed or not, this level n-
ishes when the user votes correctly a determined number of times. Finally,
the user might be a Project Leader, deciding which values committed are
accepted or rejected.
Once an user has reached one level it can come back to this level any times
as it wants.

5 How to run the Tool

As the Mudoku tool, Mudoku with Agents is using java Applets, it has been
developed with Eclipse and tested only in a Windows 7 operating System
(the working of the applets depend on the version of java used, probably in
Linux the location of the elements of the tool are moved, but it should work).
(In any case parameters are needed, but the path build will need the lo-
cation of the CHOCO library). To run the applications in the same host you
should follow the next steps:

- Run the Server Applet with default parameters.
- Run the Agents Applet and connect it to the server with default parame-
ters.
- Create a Random Community with 15-45 members.
- Run the Client (User) Applet and check the game settings appropriate for
you.
- Connect it to the server with default parameters and try to join the Com-
munity.
If the server is not located in the same host than the Clients you should
know the IP of the host and the port of the application is using in this host
(probably the default one).
