import Navbar from "@/components/Navbar";
import MovieList from "@/components/MovieList";

export default function Home() {
  return (
    <>
      <Navbar />
      <main className="flex flex-1 flex-col">
        <MovieList />
      </main>
    </>
  );
}
