package main

import (
	"encoding/json"
	"flag"
	"log"
	"net/http"
	"path/filepath"
	"strings"
)

var (
	contentDir string
	port       string
	secret      string
)

type Stream struct {
	ID   string `json:"id"`
	Type string `json:"type"`
	Name string `json:"name"`
	URL  string `json:"url"`
}

var streams = []Stream{
	{ID: "1", Name: "dikro", Type: "local", URL: "/dirko.wma"},
	{ID: "2", Name: "compile2noel", Type: "local", URL: "/pisteunique.mp3"},
	{ID: "3", Name: "soma", Type: "remote", URL: "https://ice2.somafm.com/groovesalad-256-mp3"},
	{ID: "4", Name: "france info", Type: "remote", URL: "https://stream.radiofrance.fr/franceinfo/franceinfo.m3u8"},
	{ID: "5", Name: "france inter", Type: "remote", URL: "https://stream.radiofrance.fr/franceinter/franceinter.m3u8"},
	{ID: "6", Name: "france musique", Type: "remote", URL: "https://stream.radiofrance.fr/francemusique/francemusique.m3u8"},
	{ID: "7", Name: "fip", Type: "remote", URL: "https://stream.radiofrance.fr/fip/fip.m3u8"},
	{ID: "8", Name: "nova", Type: "remote", URL: "http://novazz.ice.infomaniak.ch/novazz-128.mp3"},
}

func authMiddleware(next http.HandlerFunc) http.HandlerFunc {
	return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
		token := r.Header.Get("Authorization")
		if token != secret {
            log.Println("Unauthorized token: ", token)
			http.Error(w, "Unauthorized", http.StatusUnauthorized)
			return
		}
		next.ServeHTTP(w, r)
	})
}

func libraryHandler(w http.ResponseWriter, r *http.Request) {
	w.Header().Set("Content-Type", "application/json")
	json.NewEncoder(w).Encode(streams)
}

func contentHandler(w http.ResponseWriter, r *http.Request) {
	id := strings.TrimPrefix(r.URL.Path, "/content/")
	for _, stream := range streams {
		if stream.ID == id {
			if stream.Type == "remote" {
				http.Redirect(w, r, stream.URL, http.StatusFound)
			} else {
				http.ServeFile(w, r, filepath.Join(contentDir, stream.URL))
			}
			return
		}
	}
	http.NotFound(w, r)
}

func main() {
	flag.StringVar(&contentDir, "content", "content", "Content directory")
	flag.StringVar(&port, "port", "3254", "Port to listen on")
    flag.StringVar(&secret, "token", "123LKJH678", "Token")
	flag.Parse()

    secret = "Bearer " + secret

	http.HandleFunc("/library", authMiddleware(libraryHandler))
	http.HandleFunc("/content/", authMiddleware(contentHandler))
	log.Println("Server started on port ", port)
	http.ListenAndServe(":"+port, nil)
}
