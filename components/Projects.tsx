"use client";

import Image from "next/image";
import { motion } from "framer-motion";
import { useInView } from "framer-motion";
import { useRef } from "react";
import { ExternalLink } from "lucide-react";
import { FaGithub } from "react-icons/fa";
import portfolioImg from "./photo/portfolio.png";
import travelImg from "./photo/travel.png";
import posimg from "./photo/pos.png"
const Projects = () => {
  const ref = useRef(null);
  const isInView = useInView(ref, { once: true });

  const projects = [
    {
      title: "Travel Booking Website",
      problem: "Travelers struggle to find comprehensive booking solutions that combine flights, hotels, and activities in one platform with real-time availability and competitive pricing.",
      solution: "Developed a full-stack booking platform using React for the frontend and Node.js with MySQL for the backend, featuring real-time search, secure payment integration, and user authentication.",
      techStack: ["HTML", "CSS", "JavaScript",  "MySQL", "PHP"],
      features: ["Real-time search",  "User authentication", "Booking management", "Responsive design"],
      github: "https://github.com/princegupt1234/Travel-booking",
      demo: "https://bharattrip.42web.io/",
      image: travelImg,
    },
    {
      title: "Portfolio Website",
      problem: "Developers need a professional online presence to showcase their work, but many templates lack customization and modern design elements.",
      solution: "Created a responsive portfolio website with dark/light mode toggle, smooth animations, and optimized performance using Next.js and Tailwind CSS.",
      techStack: ["Next.js", "TypeScript", "Tailwind CSS", "Framer Motion"],
      features: ["Dark/Light mode", "Smooth animations", "Responsive design", "SEO optimized", "Fast loading"],
      github: "https://github.com/princegupt/portfolio",
      demo: "https://princegportfolio.netlify.app",
      image: portfolioImg,
    },
    {
      title: "POS System",
      problem: "Retailers need an efficient system to manage sales, inventory, and customer transactions with ease.",
      solution: "Developed a robust POS system with product management, billing, invoice generation, and customer tracking using React and Node.js.",
      techStack: ["React", "Node.js", "Express.js", "MongoDB", "JavaScript", "CSS", "HTML"],
      features: [
        "Product entry and management",
        "Customer details storage",
        "Auto-calculation of bills",
        "Invoice generation (print/PDF)",
        "Sales history tracking",
        "Keyboard shortcuts",
        "Dark theme"
      ],
      image: posimg,
      github: "https://github.com/princegupt1234/billing-website",
     
    }
  ];

  return (
    <section id="projects" className="py-20 bg-white dark:bg-gray-900">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <motion.div
          ref={ref}
          initial={{ opacity: 0, y: 50 }}
          animate={isInView ? { opacity: 1, y: 0 } : {}}
          transition={{ duration: 0.8 }}
          className="text-center mb-16"
        >
          <h2 className="text-3xl md:text-4xl font-bold text-gray-900 dark:text-white mb-4">
            Featured Projects
          </h2>
          <p className="text-lg text-gray-600 dark:text-gray-300 max-w-2xl mx-auto">
            Real-world applications built with modern technologies and best practices
          </p>
        </motion.div>

        <div className="space-y-16">
          {projects.map((project, index) => (
            <motion.div
              key={project.title}
              initial={{ opacity: 0, y: 50 }}
              animate={isInView ? { opacity: 1, y: 0 } : {}}
              transition={{ duration: 0.8, delay: index * 0.2 }}
              className="bg-gray-50 dark:bg-gray-800 rounded-2xl overflow-hidden shadow-lg hover:shadow-xl transition-shadow"
            >
              <div className="md:flex">
                <div className="md:w-1/2 p-8">
                  <div className="bg-gray-200 dark:bg-gray-700 h-48 rounded-lg mb-6 overflow-hidden">
                    {project.image ? (
                      <Image
                        src={project.image}
                        alt={`${project.title} screenshot`}
                        className="object-cover w-full h-full"
                        width={700}
                        height={420}
                      />
                    ) : (
                      <div className="flex items-center justify-center h-full">
                        <span className="text-gray-500 dark:text-gray-400">Project Screenshot</span>
                      </div>
                    )}
                  </div>
                  <div className="flex gap-4">
                    <a
                      href={project.github}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="flex items-center gap-2 px-4 py-2 bg-gray-900 dark:bg-gray-700 text-white rounded-lg hover:bg-gray-800 dark:hover:bg-gray-600 transition-colors"
                    >
                      <FaGithub size={16} />
                      Code
                    </a>
                    <a
                      href={project.demo}
                      target="_blank"
                      rel="noopener noreferrer"
                      className="flex items-center gap-2 px-4 py-2 border border-blue-600 text-blue-600 dark:text-blue-400 rounded-lg hover:bg-blue-50 dark:hover:bg-blue-900/20 transition-colors"
                    >
                      <ExternalLink size={16} />
                      Live Demo
                    </a>
                  </div>
                </div>
                <div className="md:w-1/2 p-8">
                  <h3 className="text-2xl font-bold text-gray-900 dark:text-white mb-4">
                    {project.title}
                  </h3>

                  <div className="mb-4">
                    <h4 className="text-lg font-semibold text-red-600 dark:text-red-400 mb-2">
                      Problem Statement
                    </h4>
                    <p className="text-gray-600 dark:text-gray-300 mb-4">
                      {project.problem}
                    </p>
                  </div>

                  <div className="mb-4">
                    <h4 className="text-lg font-semibold text-green-600 dark:text-green-400 mb-2">
                      Solution
                    </h4>
                    <p className="text-gray-600 dark:text-gray-300 mb-4">
                      {project.solution}
                    </p>
                  </div>

                  <div className="mb-4">
                    <h4 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                      Tech Stack
                    </h4>
                    <div className="flex flex-wrap gap-2 mb-4">
                      {project.techStack.map((tech) => (
                        <span
                          key={tech}
                          className="px-3 py-1 bg-blue-100 dark:bg-blue-900/30 text-blue-800 dark:text-blue-300 rounded-full text-sm"
                        >
                          {tech}
                        </span>
                      ))}
                    </div>
                  </div>

                  <div>
                    <h4 className="text-lg font-semibold text-gray-900 dark:text-white mb-2">
                      Key Features
                    </h4>
                    <ul className="list-disc list-inside text-gray-600 dark:text-gray-300 space-y-1">
                      {project.features.map((feature) => (
                        <li key={feature}>{feature}</li>
                      ))}
                    </ul>
                  </div>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      </div>
    </section>
  );
};

export default Projects;