#!/bin/bash

# ===================================
# CRM System Launcher
# ===================================
# Usage:
#   ./launch.sh          - запуск с H2 (по умолчанию)
#   ./launch.sh postgres - запуск с PostgreSQL
#   ./launch.sh h2       - запуск с H2

GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
RED='\033[0;31m'
NC='\033[0m'

PROFILE="${1:-h2}"

print_header() {
    echo -e "${GREEN}CRM System - $1 Mode${NC}"
    echo -e "${YELLOW}===================================${NC}"
}

launch_h2() {
    print_header "Development (H2)"
    echo -e "${YELLOW}   - DB: H2 (in-memory)${NC}"
    echo -e "${YELLOW}   - H2 Console: http://localhost:8080/h2-console${NC}"
    echo -e "${YELLOW}   - API URL: http://localhost:8080/api${NC}"
    echo -e "${YELLOW}   - Profile: h2${NC}"
    echo ""

    unset SPRING_PROFILES_ACTIVE 2>/dev/null

    echo -e "${CYAN}Launching...${NC}"
    ./gradlew bootRun --args='--spring.profiles.active=h2'

    echo -e "${RED}crm-system stopped${NC}"
}

launch_postgres() {
    print_header "Production (Postgres)"
    echo -e "${YELLOW}   - Postgres DB${NC}"
    echo -e "${YELLOW}   - API URL: http://localhost:8080/api${NC}"
    echo -e "${YELLOW}   - Profile: postgres${NC}"
    echo ""

    unset SPRING_PROFILES_ACTIVE 2>/dev/null

    echo -e "${CYAN}Starting Docker containers...${NC}"
    docker-compose up -d

    sleep 5

    echo -e "${CYAN}Launching application...${NC}"
    ./gradlew bootRun --args='--spring.profiles.active=postgres'

    echo -e "${CYAN}Stopping Docker containers...${NC}"
    docker-compose down

    echo -e "${RED}crm-system stopped (without deleting volumes)${NC}"
    echo -e "${YELLOW}If you want to delete data from docker - run: docker-compose down -v${NC}"
}

show_help() {
    echo -e "${GREEN}CRM System Launcher${NC}"
    echo "Usage:"
    echo "  ./launch.sh          - запуск с H2 (по умолчанию)"
    echo "  ./launch.sh postgres - запуск с PostgreSQL"
    echo "  ./launch.sh h2       - запуск с H2"
    echo "  ./launch.sh help     - показать эту помощь"
}

case "$PROFILE" in
    "postgres"|"prod"|"production")
        launch_postgres
        ;;
    "h2"|"dev"|"development")
        launch_h2
        ;;
    "help"|"-h"|"--help")
        show_help
        ;;
    *)
        echo -e "${RED}Unknown profile: $PROFILE${NC}"
        echo ""
        show_help
        exit 1
        ;;
esac