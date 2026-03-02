"""
Tkinter GUI Application for ${projectName}
Python's built-in cross-platform GUI framework
"""

import tkinter as tk
from tkinter import ttk, messagebox
import logging

logger = logging.getLogger(__name__)


class ${projectName?replace("_", "")?cap_first}App:
    """Main application window"""

    def __init__(self, root):
        self.root = root
        self.root.title("${projectName}")
        self.root.geometry("800x600")

        # Configure style
        self.style = ttk.Style()
        self.style.theme_use('clam')

        # Create UI
        self.create_widgets()

        logger.info("Tkinter application initialized")

    def create_widgets(self):
        """Create and layout GUI widgets"""
        # Main frame
        main_frame = ttk.Frame(self.root, padding="20")
        main_frame.grid(row=0, column=0, sticky=(tk.W, tk.E, tk.N, tk.S))

        # Configure grid weights
        self.root.columnconfigure(0, weight=1)
        self.root.rowconfigure(0, weight=1)
        main_frame.columnconfigure(0, weight=1)

        # Title
        title = ttk.Label(
            main_frame,
            text="${projectName}",
            font=('Arial', 24, 'bold')
        )
        title.grid(row=0, column=0, pady=20)

        # Input frame
        input_frame = ttk.LabelFrame(main_frame, text="Input", padding="10")
        input_frame.grid(row=1, column=0, sticky=(tk.W, tk.E), pady=10)
        input_frame.columnconfigure(1, weight=1)

        # Name input
        ttk.Label(input_frame, text="Name:").grid(row=0, column=0, sticky=tk.W, pady=5)
        self.name_var = tk.StringVar()
        name_entry = ttk.Entry(input_frame, textvariable=self.name_var, width=40)
        name_entry.grid(row=0, column=1, sticky=(tk.W, tk.E), pady=5)

        # Message input
        ttk.Label(input_frame, text="Message:").grid(row=1, column=0, sticky=tk.W, pady=5)
        self.message_var = tk.StringVar()
        message_entry = ttk.Entry(input_frame, textvariable=self.message_var, width=40)
        message_entry.grid(row=1, column=1, sticky=(tk.W, tk.E), pady=5)

        # Button frame
        button_frame = ttk.Frame(main_frame)
        button_frame.grid(row=2, column=0, pady=20)

        # Submit button
        submit_btn = ttk.Button(
            button_frame,
            text="Submit",
            command=self.on_submit
        )
        submit_btn.grid(row=0, column=0, padx=5)

        # Clear button
        clear_btn = ttk.Button(
            button_frame,
            text="Clear",
            command=self.on_clear
        )
        clear_btn.grid(row=0, column=1, padx=5)

        # Output frame
        output_frame = ttk.LabelFrame(main_frame, text="Output", padding="10")
        output_frame.grid(row=3, column=0, sticky=(tk.W, tk.E, tk.N, tk.S), pady=10)
        output_frame.columnconfigure(0, weight=1)
        output_frame.rowconfigure(0, weight=1)
        main_frame.rowconfigure(3, weight=1)

        # Text widget for output
        self.output_text = tk.Text(output_frame, height=15, width=60)
        self.output_text.grid(row=0, column=0, sticky=(tk.W, tk.E, tk.N, tk.S))

        # Scrollbar
        scrollbar = ttk.Scrollbar(output_frame, orient=tk.VERTICAL, command=self.output_text.yview)
        scrollbar.grid(row=0, column=1, sticky=(tk.N, tk.S))
        self.output_text.configure(yscrollcommand=scrollbar.set)

    def on_submit(self):
        """Handle submit button click"""
        name = self.name_var.get()
        message = self.message_var.get()

        if not name or not message:
            messagebox.showwarning("Input Required", "Please fill in all fields")
            return

        # Process data here
        output = f"Name: {name}\nMessage: {message}\n"
        self.output_text.insert(tk.END, output + "\n")
        self.output_text.see(tk.END)

        logger.info(f"Submitted: {name} - {message}")

    def on_clear(self):
        """Handle clear button click"""
        self.name_var.set("")
        self.message_var.set("")
        self.output_text.delete(1.0, tk.END)


def main():
    """Main entry point for GUI application"""
    # Configure logging
    logging.basicConfig(level=logging.INFO)

    # Create root window
    root = tk.Tk()

    # Create application
    app = ${projectName?replace("_", "")?cap_first}App(root)

    # Start event loop
    root.mainloop()


if __name__ == "__main__":
    main()
