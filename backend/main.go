package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"io"
	"log"
	"net/http"
	"os"
	"path/filepath"
)

var (
	contentDir string
	dbDir      string
	port       string
	secret     string
)

func authMiddleware(next http.HandlerFunc) http.HandlerFunc {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		token := r.Header.Get("Authorization")
		if token != secret {
			log.Println("Unauthorized token: ", token)
			log.Println("secret is: ", secret)
			http.Error(w, "Unauthorized", http.StatusUnauthorized)
			return
		}
		next.ServeHTTP(w, r)
	})
}

func updateLibraryHandler(w http.ResponseWriter, r *http.Request) {
	log.Println()
	log.Println("POST", r.URL.Path)

    project := r.PathValue("project")
	fileName := r.PathValue("id") + ".json"
    json, err := io.ReadAll(r.Body)
    log.Println("received payload: ", string(json))

    if err != nil {
        log.Println(err)
        http.Error(w, "Internal server error", http.StatusInternalServerError)
        return
    }
    _ = os.Mkdir(filepath.Join(dbDir, project), 0755)
    
    err = os.WriteFile(filepath.Join(dbDir, project, fileName), json, 0644) 
    if err != nil {
        log.Println(err)
        http.Error(w, "Internal server error", http.StatusInternalServerError)
    }
}

func getLibraryHandler(w http.ResponseWriter, r *http.Request) {
	log.Println()
	log.Println("GET ", r.URL.Path)

	w.Header().Set("Content-Type", "application/json")

    project := r.PathValue("project")
	fileName := r.PathValue("id") + ".json"

	content, err := os.ReadFile(filepath.Join(dbDir, project, fileName))
	if err != nil {
		log.Println(err)
		log.Println("returning empty json")
		json.NewEncoder(w).Encode("{}")
		return
	}

	var payload map[string]interface{}
	err = json.Unmarshal(content, &payload)
	if err != nil {
		log.Println(err)
		http.Error(w, "Internal server error", http.StatusInternalServerError)
	}

	log.Println("Returning payload: ", payload)
	json.NewEncoder(w).Encode(payload)
}

func main() {
	flag.StringVar(&contentDir, "content", "./content", "Content directory")
	flag.StringVar(&dbDir, "db", "./db", "DB directory")
	flag.StringVar(&port, "port", "3254", "Port to listen on")
	flag.StringVar(&secret, "token", "123LKJH678", "Bearer token")
	flag.Parse()

	secret = "Bearer " + secret

	http.HandleFunc("GET /library/{project}/{id}", authMiddleware(getLibraryHandler))
	http.HandleFunc("POST /library/{project}/{id}", authMiddleware(updateLibraryHandler))

	fmt.Println("Pantry started")
	log.Fatal(http.ListenAndServe(":"+port, nil))
}
