#!/usr/bin/env python3

from diagrams import Diagram, Cluster, Edge
from diagrams.programming.framework import Spring
from diagrams.onprem.database import PostgreSQL
from diagrams.onprem.client import Users
from diagrams.onprem.security import Vault
from diagrams.programming.language import Java
from diagrams.onprem.network import Internet

def create_architecture_diagram():
    with Diagram("Stock Market Microservice", 
                 filename="architecture", 
                 show=False, 
                 direction="TB",
                 graph_attr={
                     "fontsize": "24",
                     "bgcolor": "transparent",
                     "pad": "0.5",
                     "ranksep": "1.2",
                     "nodesep": "0.8"
                 }):
        
        # Client Layer
        clients = Users("API Clients\n(Mobile, Web, Postman)")
        
        # Security Layer
        security = Vault("JWT Security\n(Authentication & Authorization)")
        
        with Cluster("Spring Boot Microservice", 
                    graph_attr={"bgcolor": "lightblue", "style": "rounded"}):
            
            with Cluster("REST APIs", 
                        graph_attr={"bgcolor": "lightgreen", "style": "rounded"}):
                auth_api = Vault("Auth Controller\n• Register\n• Login\n• Validate")
                stock_api = Spring("Stock Controller\n• CRUD Operations\n• Search & Analytics\n• Batch Processing")
            
            with Cluster("Business Layer", 
                        graph_attr={"bgcolor": "lightyellow", "style": "rounded"}):
                stock_service = Java("Stock Service\n(Business Logic)")
                auth_service = Vault("User Service\n(Authentication)")
            
            with Cluster("Data Access Layer", 
                        graph_attr={"bgcolor": "lightcoral", "style": "rounded"}):
                jpa_repo = Java("JPA Repositories\n(Write Operations)")
                jdbc_repo = Java("JDBC Repositories\n(Read Operations)")
        
        # Database Layer
        with Cluster("Database Layer", 
                    graph_attr={"bgcolor": "lightpink", "style": "rounded"}):
            database = PostgreSQL("PostgreSQL\n• Production Data\n• Connection Pool")
        
        # Enhanced Connections with better styling
        clients >> Edge(
            label="HTTPS Requests", 
            color="blue", 
            style="bold"
        ) >> security
        
        security >> Edge(
            label="Authorized", 
            color="green", 
            style="bold"
        ) >> [auth_api, stock_api]
        
        auth_api >> Edge(
            label="User Auth", 
            color="orange",
            style="dashed"
        ) >> auth_service
        
        stock_api >> Edge(
            label="Business Logic", 
            color="purple",
            style="dashed"
        ) >> stock_service
        
        auth_service >> Edge(
            label="User Data", 
            color="red",
            style="bold"
        ) >> jpa_repo
        
        stock_service >> Edge(
            label="Stock Data", 
            color="red",
            style="bold"
        ) >> [jpa_repo, jdbc_repo]
        
        jpa_repo >> Edge(
            label="Write SQL", 
            color="darkgreen",
            style="bold"
        ) >> database
        
        jdbc_repo >> Edge(
            label="Read SQL", 
            color="darkgreen",
            style="dashed"
        ) >> database

if __name__ == "__main__":
    create_architecture_diagram()
    print("Architecture diagram generated as 'architecture.png'")